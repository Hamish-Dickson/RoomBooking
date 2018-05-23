package ClientModule;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

import java.sql.Timestamp;

/**
 * Class to store the properties of a booked room, extends Room.Java
 *
 * @author Hamish Dickson
 */
public class BookedRoom extends Room {
    private int bookingId;
    private SimpleIntegerProperty bookerId;
    private SimpleStringProperty bookerUsername;
    private SimpleStringProperty bookerFname;
    private SimpleStringProperty bookerLname;
    private Timestamp from;
    private Timestamp to;

    /**
     * Create an instance of Room
     *
     * @param bookingId      int the booking id
     * @param roomId         int rooms id
     * @param number         String room number(Can be Strings(i.e. Study hall, gym)
     * @param workStations   int number of PCs in the room
     * @param breakoutSeats  int number of Breakout seats in the room
     * @param equipment      String any other equipment
     * @param bookerId       int id of the user booking the room
     * @param bookerUsername String username of the user booking the room
     * @param bookerFname    String first name of the user booking the room
     * @param bookerLname    String last name of the user booking the room
     * @param from           Timestamp time the booking starts
     * @param to             Timestamp time the booking ends
     */
    public BookedRoom(int bookingId, int roomId, String number, int workStations, int breakoutSeats, String equipment, int bookerId,
                      String bookerUsername, String bookerFname, String bookerLname, Timestamp from, Timestamp to) {
        super(roomId, number, workStations, breakoutSeats, equipment);
        super.setButton(new Button("Confirm!"));
        this.bookingId = bookingId;
        this.bookerId = new SimpleIntegerProperty(bookerId);
        this.bookerFname = new SimpleStringProperty(bookerFname);
        this.bookerLname = new SimpleStringProperty(bookerLname);
        this.bookerUsername = new SimpleStringProperty(bookerUsername);
        this.from = from;
        this.to = to;
    }

    /*
    Lots of getters and setters, don't think they warrant JavaDoccing, they are required for the table view
     */
    public int getBookerId() {
        return bookerId.get();
    }

    public SimpleIntegerProperty bookerIdProperty() {
        return bookerId;
    }

    public void setBookerId(int bookerId) {
        this.bookerId.set(bookerId);
    }

    public String getBookerUsername() {
        return bookerUsername.get();
    }

    public SimpleStringProperty bookerUsernameProperty() {
        return bookerUsername;
    }

    public void setBookerUsername(String bookerUsername) {
        this.bookerUsername.set(bookerUsername);
    }

    public String getBookerFname() {
        return bookerFname.get();
    }

    public SimpleStringProperty bookerFnameProperty() {
        return bookerFname;
    }

    public void setBookerFname(String bookerFname) {
        this.bookerFname.set(bookerFname);
    }

    public String getBookerLname() {
        return bookerLname.get();
    }

    public SimpleStringProperty bookerLnameProperty() {
        return bookerLname;
    }

    public void setBookerLname(String bookerLname) {
        this.bookerLname.set(bookerLname);
    }

    public String getFrom() {
        return from.toString().substring(0, 16);
    }

    public void setFrom(Timestamp from) {
        this.from = from;
    }

    public String getTo() {
        return to.toString().substring(11, 16);
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setTo(Timestamp to) {
        this.to = to;
    }
}
