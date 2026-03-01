package av.entrance.client;

import av.entrance.client.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/login.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        LoginController controller = loader.getController();

        stage.setTitle("Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.setScene(scene);

        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    controller.handleLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        stage.getIcons().clear();
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png"))
        );

        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}
