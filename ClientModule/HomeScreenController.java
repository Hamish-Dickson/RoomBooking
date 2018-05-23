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
 * Controller class for the home.fxml file
 *
 * @author Hamish Dickson
 */
public class HomeScreenController {
    @FXML
    private Label usernameLbl;

    @FXML
    private DatePicker dp;

    @FXML
    private TableView<Room> tableView;

    @FXML
    private TableColumn<Room, String> colRoomNo, colEquipment;

    @FXML
    private TableColumn<Room, Integer> colWorkstations, colBreakSeats;

    @FXML
    public TableColumn<Room, Button> colBookRooms;

    @FXML
    private ComboBox<String> cbStartTime;

    @FXML
    private ComboBox<String> cbEndTime;

    @FXML
    private TextField capacityField;


    private static final String[] times = {"0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600"};

    private ObservableList<Room> rooms = FXCollections.observableArrayList();

    //only ever assigned, not accessed, but could be useful for future iterations
    private static PasswordScreenController passwordScreenController;

    private Session session;

    /**
     * Initializes some of the UI features
     */
    @FXML
    public void initialize() {
        dp.setShowWeekNumbers(false);
        for (int i = 0; i < times.length - 1; i++) {
            cbStartTime.getItems().add(times[i]);
        }
    }

    /**
     * Sets the end times to be after the start time
     */
    public void setEndTimes() {
        cbEndTime.getItems().removeAll(cbEndTime.getItems());
        for (String time : times) {
            if ((Integer.parseInt(time) > Integer.parseInt(cbStartTime.getValue()))) {
                cbEndTime.getItems().add(time);
            }
        }
    }

    /**
     * Sets the username label value to the current user's username
     *
     * @param uName the current user's username
     */
    public void setUsername(String uName) {
        usernameLbl.setText("Welcome: " + uName.toUpperCase());
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

    /**
     * Returns the user to the login screen and removes the current session
     */
    public void logOut() {
        UIRender.setLoginScreen();
        session = null;
    }

    /**
     * Renders the UI elements for the password.fxml file
     */
    public void changePassword() {
        Stage passwordStage = new Stage();
        FXMLLoader loader = new FXMLLoader(UIRender.class.getResource("password.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            passwordScreenController = loader.getController();
            Scene pw = new Scene(root, 600, 600);
            passwordStage.setScene(pw);
            passwordStage.show();
            passwordStage.setResizable(false);
            passwordStage.getIcons().add(new Image("file:./media/settings.png"));
        } catch (IOException e) {
            CustomAlert.alert(3, e);
        }
    }

    /**
     * @return the current session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Shows the user available rooms based on their choices
     */
    public void submit() {
        rooms.removeAll();
        tableView.getItems().clear();
        int capacity;
        int startTime;
        int endTime;
        try {
            capacity = Integer.parseInt(capacityField.getText());
            startTime = Integer.parseInt(cbStartTime.getValue());
            endTime = Integer.parseInt(cbEndTime.getValue());
        } catch (NumberFormatException e) {
            CustomAlert.alert(1, e);
            return;
        }


        DBAction dbAction = new DBAction();
        dbAction.populateRooms(rooms, capacity, dp.getValue(), startTime, endTime);
        setTableCells();
    }

    /**
     * Sets the table cells for the table view, and sets an action for the Buttons
     */
    private void setTableCells() {
        for (Room room : rooms) {
            room.getButton().setOnAction(this::bookRoom);
        }
        colRoomNo.setCellValueFactory(new PropertyValueFactory<>("number"));
        colWorkstations.setCellValueFactory(new PropertyValueFactory<>("workStations"));
        colBreakSeats.setCellValueFactory(new PropertyValueFactory<>("breakoutSeats"));
        colEquipment.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        colBookRooms.setCellValueFactory(new PropertyValueFactory<>("button"));
        tableView.setItems(rooms);
    }

    /**
     * Books a room based on user selection
     *
     * @param actionEvent ActionEvent the event that called this method
     */
    private void bookRoom(ActionEvent actionEvent) {
        String roomNoTemp = "";
        int roomId = 0;

        for (Room room : rooms) {
            if (actionEvent.getSource().equals(room.getButton())) {//compares the selected button to each button in rooms
                roomNoTemp = room.getNumber();
                roomId = room.getId();
            }
        }

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you want to book room: " + roomNoTemp
                        + "\non: " + dp.getValue()
                        + "\nfrom: " + cbStartTime.getValue()
                        + "\nto: " + cbEndTime.getValue(),
                ButtonType.YES, ButtonType.NO);

        alert.setTitle("Are you sure?");

        int finalRoomId = roomId;
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DBAction dbAction = new DBAction();
                dbAction.recordSale(finalRoomId, dp.getValue(), cbStartTime.getValue(), cbEndTime.getValue(), session.getUsername());
                submit();
                Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Successfully booked room!");
                success.setTitle("success");
                success.show();
            }
        });


    }


}
