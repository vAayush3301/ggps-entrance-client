package av.entrance.client.controller.user;

import av.entrance.client.model.Test;
import av.entrance.client.server.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

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
        stage.centerOnScreen();
    }

    private void showInstructions(Test test) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exam Instructions");
        alert.setHeaderText("Please read carefully");

        String content = String.format("""
                Exam Pattern:
                • %d Questions (MCQs)
                • +4 for correct, -1 for wrong
                • No reattempt after submission
                
                Duration:
                • %d minutes
                • Timer will auto-submit when time ends
                
                Rules:
                • Switching tabs or losing focus 3 times will auto-submit
                • Minimizing the window counts as focus loss
                • Do not refresh or close the application
                
                Click OK to begin.
                """, test.getQuestions().size(), test.getDuration());

        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

        alert.showAndWait();
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

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

            alert.showAndWait();

            return;
        }
        try {
            showInstructions(test);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/user/exam_page.fxml"));
            BorderPane root = loader.load();

            ExamController examController = loader.getController();
            examController.setUserID(userID);
            examController.setTestIp(ip);
            examController.setTestPort(port);
            examController.setTest(test);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene scene = new Scene(scrollPane);

            Stage stage = (Stage) responseLabel.getScene().getWindow();

            AtomicInteger focusLostCount = new AtomicInteger(0);

            stage.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                if (!t1 && stage.getScene() == scene) {
                    focusLostCount.incrementAndGet();

                    if (focusLostCount.intValue() >= 3) {
                        try {
                            examController.submit("You have been disqualified. Submitting your response.");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            stage.iconifiedProperty().addListener((observableValue, aBoolean, t1) -> {
                if (!t1 && stage.getScene() == scene) {
                    focusLostCount.incrementAndGet();

                    if (focusLostCount.intValue() >= 3) {
                        try {
                            examController.submit("You have been disqualified. Submitting your response.");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

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
