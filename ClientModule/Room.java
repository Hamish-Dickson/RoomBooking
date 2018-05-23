package ClientModule;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

/**
 * Class to store the properties of rooms
 *
 * @author Hamish Dickson
 */
public class Room {
    private SimpleIntegerProperty id;
    private SimpleStringProperty number;
    private SimpleIntegerProperty workStations;
    private SimpleIntegerProperty breakoutSeats;
    private SimpleStringProperty equipment;
    private Button button;

    /**
     * Create an instance of Room
     *
     * @param id            int rooms id
     * @param number        String room number(Can be Strings(i.e. Study hall, gym)
     * @param workStations  int number of PCs in the room
     * @param breakoutSeats int number of Breakout seats in the room
     * @param equipment     String any other equipment
     */
    public Room(int id, String number, int workStations, int breakoutSeats, String equipment) {
        this.id = new SimpleIntegerProperty(id);
        this.number = new SimpleStringProperty(number);
        this.workStations = new SimpleIntegerProperty(workStations);
        this.breakoutSeats = new SimpleIntegerProperty(breakoutSeats);
        this.equipment = new SimpleStringProperty(equipment);
        this.button = new Button("Book!");
    }


    /*
    Lots of getters and setters, don't think they warrant JavaDoccing, they are required for the table view
     */
    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public int getId() {
        return id.get();
    }


    public String getNumber() {
        return number.get();
    }

    public int getWorkStations() {
        return workStations.get();
    }

    public int getBreakoutSeats() {
        return breakoutSeats.get();
    }

    public String getEquipment() {
        return equipment.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public void setWorkStations(int workStations) {
        this.workStations.set(workStations);
    }

    public void setBreakoutSeats(int breakoutSeats) {
        this.breakoutSeats.set(breakoutSeats);
    }

    public void setEquipment(String equipment) {
        this.equipment.set(equipment);
    }
}
