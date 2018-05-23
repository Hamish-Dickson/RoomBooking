package ClientModule;

import Database.DBAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the admin.fxml file
 *
 * @author Hamish Dickson
 */

public class AdminScreenController {
    @FXML
    private Label usernameLbl;

    @FXML
    private TableView<BookedRoom> tableView;

    @FXML
    private TableColumn<BookedRoom, String> colRoomNo, colEquipment, colBookerUsername, colFname, colLname, colFrom, colTo;

    @FXML
    private TableColumn<BookedRoom, Integer> colWorkstations, colBreakSeats;

    @FXML
    private TableColumn<BookedRoom, Button> colConfirm;

    @FXML
    private Label title;

    private ObservableList<BookedRoom> bookedRooms = FXCollections.observableArrayList();

    private Session session;
    private ManageScreenController manageScreenController;

    /**
     * Initializes some of the initial data values and screen elements.
     */
    @FXML
    public void initialize() {
        bookedRooms.removeAll();
        DBAction dbaction = new DBAction();
        dbaction.populateBookings(bookedRooms, false);
        setTableCells();
    }

    /**
     * Sets the cell values fo the table cells, and sets the action to be performed on button click
     */
    private void setTableCells() {
        for (BookedRoom room : bookedRooms) {
            room.getButton().setOnAction(this::confirmBooking);//method reference instead of lambda, thanks intelliJ
        }

        colRoomNo.setCellValueFactory(new PropertyValueFactory<>("number"));
        colWorkstations.setCellValueFactory(new PropertyValueFactory<>("workStations"));
        colBreakSeats.setCellValueFactory(new PropertyValueFactory<>("breakoutSeats"));
        colEquipment.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        colConfirm.setCellValueFactory(new PropertyValueFactory<>("button"));
        colBookerUsername.setCellValueFactory(new PropertyValueFactory<>("bookerUsername"));
        colFname.setCellValueFactory(new PropertyValueFactory<>("bookerFname"));
        colLname.setCellValueFactory(new PropertyValueFactory<>("bookerLname"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("to"));
        tableView.setItems(bookedRooms);
    }

    /**
     * Confirms the selected booking
     *
     * @param actionEvent event that activated the button
     */
    private void confirmBooking(ActionEvent actionEvent) {
        String roomNoTemp = "";
        int bookingId = 0;
        String fname = null;
        String lname = null;
        String from = null;
        String to = null;

        for (BookedRoom room : bookedRooms) {
            if (actionEvent.getSource().equals(room.getButton())) {//compares the selected button to each button in rooms
                roomNoTemp = room.getNumber();
                bookingId = room.getBookingId();
                fname = room.getBookerFname();
                lname = room.getBookerLname();
                from = room.getFrom();
                to = room.getTo();
            }
        }

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you want confirm booking for room: " + roomNoTemp
                        + "\nto: " + fname + " " + lname
                        + "\nfrom: " + from
                        + "\nto: " + to,
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Booking?");

        int finalBookingId = bookingId;//effective final value to put into lambda expression
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DBAction dbAction = new DBAction();
                dbAction.confirmBooking(finalBookingId);
                repopulate(false);
            }
        });
    }

    /**
     * Repopulates the ObservableList with updated values
     *
     * @param type report type, 0 for unconfirmed bookings, 1 for confirmed bookings
     */
    private void repopulate(boolean type) {
        bookedRooms.removeAll();
        tableView.getItems().clear();
        bookedRooms.removeAll();
        DBAction dbAction = new DBAction();
        dbAction.populateBookings(bookedRooms, type);
        setTableCells();
    }

    /**
     * Switches the type of report the user is seeing each time the button is pressed
     */
    @FXML
    public void switchReport() {
        if (title.getText().equals("Unconfirmed Booking Requests")) {
            title.setText("Confirmed Booking Requests");
            repopulate(true);
            for (Room room : bookedRooms) {
                room.setButton(null);//disable the buttons when viewing confirmed bookings
            }
        } else {
            title.setText("Unconfirmed Booking Requests");
            repopulate(false);
        }
    }

    /**
     * Creates a session for the current user
     *
     * @param username the username of the current user
     * @param admin    the admin status of the current user
     */
    public void createSession(String username, boolean admin) {
        session = new Session(username, admin);
    }

    public void setUsername(String username) {
        usernameLbl.setText("Welcome: " + username.toUpperCase());
    }

    /**
     * @return the current session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Renders the UI elements for the manage screen
     */
    public void manageAccounts() {
        Stage passwordStage = new Stage();
        FXMLLoader loader = new FXMLLoader(UIRender.class.getResource("manage.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            manageScreenController = loader.getController();
            Scene manage = new Scene(root, 600, 600);
            passwordStage.setScene(manage);
            passwordStage.show();
            passwordStage.setResizable(false);
            passwordStage.getIcons().add(new Image("file:./media/admin.png"));
        } catch (IOException e) {
            CustomAlert.alert(3, e);
        }
    }

    /**
     * Returns the user to the login screen and removes the current session
     */
    public void logOut() {
        UIRender.setLoginScreen();
        session = null;
    }
}

