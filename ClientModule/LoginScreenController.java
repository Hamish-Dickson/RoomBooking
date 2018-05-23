package ClientModule;

import Database.DBAction;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * Controller class for the login.fxml file
 *
 * @author Hamish Dickson
 */
public class LoginScreenController {
    @FXML
    private TextField uNameField;

    @FXML
    private PasswordField pwField;

    /**
     * Logs the user into the system
     */
    @FXML
    void login() {
        DBAction dbAction = new DBAction();
        dbAction.login(uNameField.getText(), pwField.getText());
    }


}
