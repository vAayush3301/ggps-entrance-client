package av.entrance.client.controller;

import av.entrance.client.controller.user.DashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private static final String ADMIN_USERID = "admin0";
    private static final String ADMIN_PWD = "null@00";
    public Button info;
    @FXML
    private TextField useridField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label responseLabel;

    @FXML
    public void handleLogin() throws IOException {
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
            stage.centerOnScreen();
        } else if (!password.isEmpty()) {
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
            stage.centerOnScreen();
        }
    }

    public void showInfo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/info.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("ExamDesk - Info");
        stage.setResizable(false);

        stage.getIcons().clear();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/av.png")));

        stage.show();
        stage.centerOnScreen();
    }

    @FXML
    public void initialize() {
        SVGPath infoPath = new SVGPath();
        infoPath.setContent("M11 11h1v5.5m0 0h1.5m-1.5 0h-1.5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9.5-4v-.5h.5V8h-.5Z");
        infoPath.setStroke(Color.BLACK);
        infoPath.setStrokeLineCap(StrokeLineCap.ROUND);
        infoPath.setStrokeLineJoin(StrokeLineJoin.ROUND);
        infoPath.setStrokeWidth(2);
        infoPath.setFill(null);
        info.setGraphic(infoPath);
        info.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        info.setStyle("-fx-background-color: #ffffff00;");
    }
}
