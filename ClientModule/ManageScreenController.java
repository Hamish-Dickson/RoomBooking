package ClientModule;

import Database.DBAction;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller class for the manage.fxml file
 *
 * @author Hamish Dickson
 */
public class ManageScreenController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox<String> cbAdmin;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField deleteUsernameField;

    private String[] adminChoices = new String[]{"Yes", "No"};


    /**
     * Initializes some of the initial UI elements
     */
    @FXML
    public void initialize() {
        cbAdmin.getItems().addAll(adminChoices);
    }

    /**
     * Adds a user to the system
     */
    @FXML
    void addUser() {
        boolean validInput = validateAddUser();
        if (validInput) {
            DBAction dbAction = new DBAction();
            dbAction.addUser(userNameField.getText(), passwordField.getText(), firstNameField.getText(),
                    lastNameField.getText(), cbAdmin.getValue());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully added user "
                    + userNameField.getText(), ButtonType.OK);
            alert.setTitle("Success");
            alert.show();
        } else {
            CustomAlert.alert(1);
        }
    }

    /**
     * Validates the input fields and checks they are not null
     *
     * @return boolean true if inputs are valid, false otherwise
     */
    private boolean validateAddUser() {
        boolean valid = false;
        if (userNameField.getText().trim().equals("") || passwordField.getText().trim().equals("")
                || firstNameField.getText().trim().equals("") || lastNameField.getText().trim().equals("")
                || cbAdmin.getValue().trim().equals("")) {
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    /**
     * Deletes a user from the system
     */
    @FXML
    void deleteUser() {
        if (!deleteUsernameField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user: "
                    + deleteUsernameField.getText() + "?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirm deletion");
            alert.setHeaderText("Are you sure?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DBAction dbAction = new DBAction();
                    dbAction.deleteUser(deleteUsernameField.getText());
                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Successfully deleted user!", ButtonType.OK);
                    success.setTitle("Success");
                    success.show();
                }
            });
        } else {
            CustomAlert.alert(1);
        }
    }

}
