package av.entrance.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class LoginController {
    private static final String ADMIN_USERID = "admin0";
    private static final String ADMIN_PWD = "test";
    @FXML private TextField useridField;
    @FXML private PasswordField passwordField;
    @FXML private Label responseLabel;

    @FXML
    private void handleLogin() {
        String user = useridField.getText();
        String password = passwordField.getText();

        if (user.isEmpty()) {
            responseLabel.setText("Please enter your User ID");
            return;
        }

        if (user.equals(ADMIN_USERID) && password.equals(ADMIN_PWD)) {
            responseLabel.setText("Admin Logged In");
        } else if (!password.isEmpty()){
            responseLabel.setText("Invalid Password");
        } else {
            responseLabel.setText("User Logged in");
        }
    }

}
