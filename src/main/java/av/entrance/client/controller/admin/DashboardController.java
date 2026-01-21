package av.entrance.client.controller.admin;

import av.entrance.client.controller.user.items.TestRowController;
import av.entrance.client.model.Test;
import av.entrance.client.service.DownloadTestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    public ListView createdTests;
    @FXML
    Button logout;

    @FXML
    public void initialize() {
        DownloadTestService downloadTest = new DownloadTestService();

        downloadTest.setOnSucceeded(event -> {
            ObservableList<Test> tests = FXCollections.observableArrayList(downloadTest.getValue());

            createdTests.setItems(tests);
        });

        downloadTest.start();

        createdTests.setCellFactory(listView -> new ListCell<Test>() {
            @Override
            protected void updateItem(Test test, boolean empty) {
                super.updateItem(test, empty);

                if (empty || test == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/items/test_row.fxml"));
                        HBox root = loader.load();

                        TestRowController controller = loader.getController();
                        controller.setTest(test);
                        controller.setIndex(createdTests.getItems().indexOf(test));

                        createdTests.prefWidthProperty().bind(root.widthProperty());

                        setGraphic(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.show();
    }

    public void createTest() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/new_test.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.show();
    }
}
