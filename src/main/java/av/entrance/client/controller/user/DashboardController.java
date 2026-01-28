package av.entrance.client.controller.user;

import av.entrance.client.model.Test;
import av.entrance.client.server.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardController {
    private static final ObjectMapper mapper = new ObjectMapper();
    public String userID;
    public Label responseLabel;
    public TextField testIpField;
    public TextField testPortField;
    @FXML
    Button logout;

    @FXML
    private void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.show();
    }

    @FXML
    public void initialize() {

    }

    public void attemptTest() {
        String ip = testIpField.getText();
        String port = testPortField.getText();

        HttpURLConnection con;
        Test test;
        try {
            URL url = new URL("http://%s:%s/api/getTest".formatted(ip, port));
            con = (HttpURLConnection) url.openConnection();


            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            InputStream inputStream = con.getInputStream();
            Payload payload = mapper.readValue(inputStream, Payload.class);

            test = payload.getTest();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Connection Error");
            alert.setContentText("Cannot connect to the test server. Verify IP address and port.");

            alert.showAndWait();

            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/user/exam_page.fxml"));
            BorderPane root = loader.load();

            ExamController examController = loader.getController();
            examController.setTest(test);
            examController.setUserID(userID);
            examController.setTestIp(ip);
            examController.setTestPort(port);

            Scene scene = new Scene(root);

            Stage stage = (Stage) responseLabel.getScene().getWindow();

            stage.setTitle(test.getTestName());
            stage.setScene(scene);

            stage.setFullScreen(true);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

            stage.setResizable(false);
            stage.show();
            stage.setFullScreenExitHint("");

            stage.fullScreenProperty().addListener((obs, wasFullScreen, isFullScreen) -> {
                if (!isFullScreen) {
                    stage.setFullScreen(true);
                }
            });

            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    keyEvent.consume();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
