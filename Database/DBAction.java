package Database;


import ClientModule.BookedRoom;
import ClientModule.CustomAlert;
import ClientModule.Room;
import ClientModule.UIRender;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Manages the execution of actions on the database
 *
 * @author Hamish Dickson
 */
public class DBAction {
    private static final DBConnectionManager dbConnectionManager = new DBConnectionManager();

    /**
     * Logs the user into the system
     *
     * @param username the username of the user logging in
     * @param password the provided password of the user logging in
     */
    public void login(String username, String password) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;

        try {
            statement = conn.createStatement();
            ResultSet rs = null;

            rs = statement.executeQuery(("SELECT password FROM users WHERE username='" + username + "';"));

            if (!rs.next()) {//if the username was invalid
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username invalid, please try again!"
                        , ButtonType.CLOSE);
                alert.setHeaderText("Error");
                alert.setTitle("Error");
                alert.show();

            } else {//if the username was valid
                if (rs.getString("password").equals(password)) {
                    boolean admin = getAdmin(username);
                    if (admin) {
                        UIRender.setAdminScreen(username, true);
                    } else {
                        UIRender.setHomeScreen(username, true);
                        UIRender.getHomeScreenController().createSession(username, admin);//creates a session for this user
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Password error, please try again! "
                            , ButtonType.CLOSE);
                    alert.setHeaderText("Error");
                    alert.setTitle("Error");
                    alert.show();
                }
            }
            conn.close();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }

    /**
     * Gets the password for the provided username
     *
     * @param username
     * @return
     */
    public String getPassword(String username) {
        String password = "";
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;

        try {
            statement = conn.createStatement();
            ResultSet rs = null;

            rs = statement.executeQuery(("SELECT password FROM users WHERE username='" + username + "';"));

            if (!rs.next()) {//if the username was invalid
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username error, please try again!"
                        , ButtonType.CLOSE);
                alert.setHeaderText("Error");
                alert.setTitle("Error");
                alert.show();
            } else {//if the username was valid
                password = rs.getString("password");
            }
            conn.close();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
        return password;
    }

    /**
     * Finds out if the user logged in is an admin
     *
     * @param username the username to get admin for
     * @return true if the user is an admin, else false
     */
    private boolean getAdmin(String username) {
        boolean admin = false;
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roomBooking");

            ResultSet rs = statement.executeQuery("SELECT admin FROM users where username= '" + username + "';");

            rs.next();

            admin = rs.getBoolean("Admin");

            conn.close();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
        return admin;
    }

    /**
     * Updates the password for the given user
     *
     * @param username the user to update's username
     * @param password the new password for this user
     */
    public void updatePassword(String username, String password) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roomBooking");

            statement.executeUpdate("UPDATE users SET password = '" + password + "' WHERE username = '"
                    + username + "';");

            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }

    /**
     * populates the ObservableList with available rooms
     *
     * @param rooms     ObservableList the rooms to add to
     * @param capacity  int the required capacity of each room
     * @param date      LocalDate the date of the requested booking
     * @param startTime int the start time of the requested booking
     * @param endTime   int the end time of the requested booking
     */
    public void populateRooms(ObservableList<Room> rooms, int capacity, LocalDate date, int startTime, int endTime) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;

        String preformatStart;

        if (startTime == 900) {//if the starTime is 9AM, the leading 0 will be cut off, so readjust that
            preformatStart = "0900";
        } else {
            preformatStart = String.valueOf(startTime);
        }
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roomBooking");

            //this statement may need some explaining
            //selects everything from rooms_new where the workstation requirement is met
            //AND where the room is not booked on the given DateTime
            ResultSet rs = statement.executeQuery("SELECT * FROM rooms_new r WHERE workstations >= " + capacity + " AND" +
                    "(SELECT count(*) FROM booked_rooms WHERE booked_rooms.room_id = r.room_id AND " +
                    "booked_rooms.date BETWEEN STR_TO_DATE('" + date + "-" + preformatStart + "','%Y-%m-%d-%k%i') " +
                    "AND STR_TO_DATE('" + date + "-" + endTime + "','%Y-%m-%d-%k%i') AND " +
                    "booked_rooms.date BETWEEN STR_TO_DATE('" + date + "-" + preformatStart + "','%Y-%m-%d-%k%i') " +
                    "AND STR_TO_DATE('" + date + "-" + endTime + "','%Y-%m-%d-%k%i')  )=0;");

            while (rs.next()) {//while there are more rooms
                rooms.add(new Room(rs.getInt("room_id"), rs.getString("room_number"), rs.getInt("workstations"),
                        rs.getInt("breakout_seats"), rs.getString("equipment")));
            }
            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }

    /**
     * Records a "sale" of a room to the customer
     *
     * @param finalRoomId int the room id to book for
     * @param date        LocalDate the date to book the room on
     * @param startTime   String the start time to book from
     * @param endTime     String the end time to book until
     * @param username    String the users username
     */
    public void recordSale(int finalRoomId, LocalDate date, String startTime, String endTime, String username) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roomBooking");

            statement.executeUpdate("INSERT INTO booked_rooms(user_id, room_id, date, end_time)" +
                    "VALUES (" + getID(username) + ", " + finalRoomId + ",STR_TO_DATE('" + date + "-" + startTime + "','%Y-%m-%d-%H%i')"
                    + ", STR_TO_DATE('" + date + "-" + endTime + "','%Y-%m-%d-%H%i'))");
            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }

    /**
     * Gets the id for the given username, this is fine as users.username is a unique column
     *
     * @param username String the username to get id for
     * @return int the user's id
     */
    private int getID(String username) {
        int userId = 0;

        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roombooking");

            ResultSet rs = statement.executeQuery("SELECT id FROM users WHERE username='" + username + "';");

            while (rs.next()) {//while there are more user IDs (there should only be 1)
                userId = rs.getInt("id");
            }

            conn.close();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
        return userId;
    }

    /**
     * Populates the ObservableList with booked rooms, confirmed or not confirmed
     *
     * @param bookedRooms ObservableList the list to populate with booked rooms
     * @param confirmed   boolean whether the desired bookings are confirmed or not
     */
    public void populateBookings(ObservableList<BookedRoom> bookedRooms, boolean confirmed) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        Statement roomsStatement = null;
        Statement usersStatement = null;
        ResultSet rs = null;
        ResultSet roomsRs = null;
        ResultSet usersRs = null;
        try {
            roomsStatement = conn.createStatement();
            usersStatement = conn.createStatement();
            statement = conn.createStatement();

            rs = statement.executeQuery("SELECT * FROM booked_rooms WHERE confirmed = " + confirmed + ";");

            while (rs.next()) {//while there are more bookings
                roomsRs = roomsStatement.executeQuery("SELECT * from rooms_new WHERE room_id = "
                        + rs.getInt("room_id") + ";");
                usersRs = usersStatement.executeQuery("SELECT id, username, fname, lname FROM users WHERE id = "
                        + rs.getInt("user_id") + ";");
                roomsRs.next();
                usersRs.next();

                bookedRooms.add(new BookedRoom(rs.getInt("booking_id"), roomsRs.getInt("room_id"),
                        roomsRs.getString("room_number"), roomsRs.getInt("workstations"),
                        roomsRs.getInt("breakout_seats"), roomsRs.getString("equipment"),
                        usersRs.getInt("id"), usersRs.getString("username"),
                        usersRs.getString("fname"), usersRs.getString("lname"),
                        rs.getTimestamp("date"), rs.getTimestamp("end_time")));

            }
            roomsStatement.close();
            usersStatement.close();
            statement.close();
            rs.close();
            if (roomsRs != null) {
                roomsRs.close();
                usersRs.close();
            }
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }

    }

    /**
     * Confirms a booking
     *
     * @param bookingId int the booking id to confirm
     */
    public void confirmBooking(int bookingId) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roombooking");

            statement.executeUpdate("UPDATE booked_rooms SET confirmed = 1 WHERE booking_id = '" + bookingId + "';");

            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }


    /**
     * Adds a user to the system
     *
     * @param username  String the new user's username
     * @param password  String the new user's password
     * @param firstName String the new user's first name
     * @param lastName  String the new user's last name
     * @param admin     String the new user's admin status
     */
    public void addUser(String username, String password, String firstName, String lastName, String admin) {
        boolean adminFinal = false;
        if (admin.equals("Yes")) {//convert the String values to booleans
            adminFinal = true;
        }

        Connection conn = dbConnectionManager.connect();
        Statement statement = null;

        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roombooking");

            statement.executeUpdate("INSERT INTO users(username, password, fname, lname, admin)" +
                    " VALUES ('" + username + "', '" + password + "', '" + firstName + "', '" + lastName + "', " + adminFinal + ");");

            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }

    /**
     * Deletes a given user from the system, foreign key on booked_rooms has ON DELETE CASCADE constraint, so will remove any bookings made by the user too
     *
     * @param username String the username of the user to delete
     */
    public void deleteUser(String username) {
        Connection conn = dbConnectionManager.connect();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            statement.executeQuery("USE roombooking");

            statement.executeUpdate("DELETE FROM users WHERE username = '" + username + "';");

            conn.close();
            statement.close();
        } catch (SQLException e) {
            CustomAlert.alert(2, e);
        }
    }
}
