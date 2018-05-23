package ClientModule;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashMap;

/**
 * Class to create custom error messages using an error code mapped to an error message
 *
 * @author Hamish Dickson
 */
public class CustomAlert {
    private static HashMap<Integer, String> errorCodes = new HashMap<>();

    /**
     * Called on system startup to initialize the values of error messages
     */
    static void initializeHashMap() {
        errorCodes.put(1, "Input Error! ");
        errorCodes.put(2, "SQL Error! ");
        errorCodes.put(3, "IO Error! ");
        errorCodes.put(4, "Database Error! ");
        errorCodes.put(5, "Critical Error! please contact system administrator ");
    }

    /**
     * Creates an alert using the error code and the message provided in the exception
     *
     * @param errorCode int the error code that has occurred
     * @param e         Exception the exception that has occurred
     */
    public static void alert(int errorCode, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorCodes.get(errorCode) + e.getMessage() + " ", ButtonType.CLOSE);
        showAlert(alert);
    }

    /**
     * Creates an alert using the error code
     *
     * @param errorCode int the error code that has occurred
     */
    public static void alert(int errorCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorCodes.get(errorCode) + " ", ButtonType.CLOSE);
        showAlert(alert);
    }

    /**
     * Sets the title, header text and shows the alert that is passed in
     *
     * @param alert Alert the alert to display
     */
    private static void showAlert(Alert alert) {
        alert.setTitle("Error!");
        alert.setHeaderText("An error has occurred");
        alert.show();
    }
}
