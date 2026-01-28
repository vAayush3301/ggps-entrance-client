package av.entrance.client.controller.user.items;

import av.entrance.client.controller.user.ExamController;
import av.entrance.client.model.Test;
import av.entrance.client.server.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestRowController {
    public Label testName;
    public Label sNo;
    public Button attemptTest;
    public Label duration;

    private Test test;

    private Server server;

    public void attempt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/user/exam_page.fxml"));
            BorderPane root = loader.load();

            ExamController examController = loader.getController();
            examController.setTest(test);

            Scene scene = new Scene(root);

            Stage stage = (Stage) sNo.getScene().getWindow();

            stage.setTitle(test.getTestName());
            stage.setScene(scene);

            stage.setFullScreen(true);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTest(Test test) {
        this.test = test;
        testName.setText(test.getTestName());
    }

    public void setIndex(int index) {
        sNo.setText(index + 1 + ".");
    }

    public void delete() {
    }

    public void host_and_end() throws Exception {
        if (server == null) {
            server = new Server(5440, test);
            server.start();
            System.out.println("Server started...");
        } else {
            server.stop();
            System.out.println("Server stopped...");
        }
    }
}
