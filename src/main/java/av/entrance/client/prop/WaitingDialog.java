package av.entrance.client.prop;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class WaitingDialog {
    private Stage dialogStage;
    @FXML
    private Label messageLabel;

    public WaitingDialog() {
    }

    public WaitingDialog(String message) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/prop/waiting_dialog.fxml"));
            Parent root = loader.load();

            messageLabel = (Label) loader.getNamespace().get("messageLabel");
            messageLabel.setText(message);

            dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Please Wait");
            dialogStage.setScene(new Scene(root));
            dialogStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/av/entrance/client/styles/dialog.css")).toExternalForm());

            dialogStage.setOnCloseRequest(event -> event.consume());
    }

    public <T> int runTask(Task<T> task) {
        final int[] responseCode = new int[1];

        task.setOnSucceeded(e -> {
            responseCode[0] = 200;
            dialogStage.close();
        });
        task.setOnFailed(e -> {
            responseCode[0] = 500;
            dialogStage.close();
        });
        new Thread(task).start();
        dialogStage.showAndWait();

        return responseCode[0];
    }
}
