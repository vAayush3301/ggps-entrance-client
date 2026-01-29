package av.entrance.client.controller;

import av.entrance.client.controller.user.DashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private static final String ADMIN_USERID = "admin0";
    private static final String ADMIN_PWD = "null@00";
    @FXML private TextField useridField;
    @FXML private PasswordField passwordField;
    @FXML private Label responseLabel;

    @FXML
    private void handleLogin() throws IOException {
        String user = useridField.getText();
        String password = passwordField.getText();

        if (user.isEmpty()) {
            responseLabel.setText("Please enter your User ID");
            return;
        }

        if (user.equals(ADMIN_USERID) && password.equals(ADMIN_PWD)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/dashboard.fxml"));
            Parent root = loader.load();

            responseLabel.setText("Admin Logged In");

            Stage stage = (Stage) useridField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
        } else if (!password.isEmpty()){
            responseLabel.setText("Invalid Password");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/user/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController userDashboard = loader.getController();
            userDashboard.userID = user;

            responseLabel.setText("User Logged In");

            Stage stage = (Stage) useridField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
            stage.setResizable(true);
            stage.show();
        }
    }

}
