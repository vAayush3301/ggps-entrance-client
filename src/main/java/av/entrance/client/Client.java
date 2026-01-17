package av.entrance.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/av/entrance/client/login.fxml")));
        scene.getStylesheets().add(getClass().getResource("/av/entrance/client/styles/login_style.css").toExternalForm());

        stage.setTitle("Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.setScene(scene);

//        stage.setResizable(false);
//        stage.sizeToScene();
        stage.show();
    }
}
