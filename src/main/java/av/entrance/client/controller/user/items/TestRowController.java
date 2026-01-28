package av.entrance.client.controller.user.items;

import av.entrance.client.model.Test;
import av.entrance.client.server.Server;
import av.entrance.client.service.DeleteTestService;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

public class TestRowController {
    public Label testName;
    public Label sNo;
    public Label duration;
    public Button hostTest, deleteTest;
    public Label details;

    private Test test;

    private Server server;

    private ObservableList<Test> backingList;

    public void setData(Test test, ObservableList<Test> backingList) {
        this.test = test;
        this.backingList = backingList;
        testName.setText(test.getTestName());
        duration.setText(test.getDuration() + " Minute(s)");
    }

    public void setIndex(int index) {
        sNo.setText(index + 1 + ".");
    }

    public void delete() {
        DeleteTestService service = new DeleteTestService(test);
        service.setOnSucceeded(e -> {
            String response = service.getValue();
            System.out.println("Server response: " + response);
            backingList.remove(test);
        });

        service.setOnFailed(e -> {
            Throwable ex = service.getException();
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to delete Test.");

            alert.showAndWait();
        });

        service.start();
    }

    public void host_and_end() throws Exception {
        if (server == null || !server.isStarted()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Host Test");
            dialog.setHeaderText("Enter Port");
            dialog.setContentText("Port:");

            Optional<String> result = dialog.showAndWait();

            String value;
            if (result.isPresent()) {
                value = result.get();

                try {
                    server = new Server(Integer.parseInt(value), test);
                    server.start();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Port");
                    alert.setContentText("Port must contain characters between 0-9.");

                    alert.showAndWait();

                    return;
                } catch (BindException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Port Already in use");
                    alert.setContentText("This port is already in use. Please try something different.");

                    alert.showAndWait();

                    return;
                }
            } else return;

            hostTest.setText("Stop");
            hostTest.setStyle("-fx-background-color: #bb2121;");
            System.out.println("Server started...");

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                String ip = socket.getLocalAddress().getHostAddress();

                details.setText(ip + ":" + value);
            }

            deleteTest.setDisable(true);
            deleteTest.setStyle("-fx-background-color: #c5c5c5;");
        } else {
            server.stop();
            hostTest.setText("Host");
            hostTest.setStyle("-fx-background-color: #4f46e5;");

            details.setText("");
            deleteTest.setDisable(false);
            deleteTest.setStyle("-fx-background-color: #4f46e5;");
            System.out.println("Server stopped...");
        }
    }
}
