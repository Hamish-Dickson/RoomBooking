package ClientModule;

import Database.DBAction;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

/**
 * Controller class for the password.fxml file
 *
 * @author Hamish Dickson
 */
public class PasswordScreenController {
    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Changes the current users password
     */
    @FXML
    void changePassword() {
        DBAction dbAction = new DBAction();
        if (currentPasswordField.getText()
                .equals(dbAction.getPassword(UIRender.getHomeScreenController()
                        .getSession().getUsername()))) {//if the user's current password is correct
            if (newPasswordField.getText().equals(confirmPasswordField.getText())) {//if the new password matches the confirm password
                dbAction.updatePassword(UIRender.getHomeScreenController().getSession().getUsername()
                        , newPasswordField.getText());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully updated password for user: "
                        + UIRender.getHomeScreenController().getSession().getUsername(), ButtonType.OK);
                alert.setHeaderText("Password updated");
                alert.setTitle("Success");
                alert.show();
            } else {
                CustomAlert.alert(1);
            }
        } else {
            CustomAlert.alert(1);
        }

    }

}
