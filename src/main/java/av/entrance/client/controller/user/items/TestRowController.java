package av.entrance.client.controller.user.items;

import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.Test;
import av.entrance.client.model.UserResult;
import av.entrance.client.prop.ResultEvaluator;
import av.entrance.client.server.Server;
import av.entrance.client.service.DeleteTestService;
import av.entrance.client.service.GetResultsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

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

                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

                    alert.showAndWait();

                    return;
                } catch (BindException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Port Already in use");
                    alert.setContentText("This port is already in use. Please try something different.");

                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

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

    public void get_results() {
        GetResultsService resultsService = new GetResultsService(test.getTestId());

        resultsService.setOnSucceeded(workerStateEvent -> {
            ObservableList<SubmitResponse> responses = FXCollections.observableArrayList(resultsService.getValue());

            ResultEvaluator evaluator = new ResultEvaluator(responses);
            ObservableList<UserResult> userResults = FXCollections.observableArrayList(evaluator.getTotalResult());

            TableView<UserResult> table = createTable(userResults);

            BorderPane root = new BorderPane();
            root.setCenter(table);

            HBox topBar = new HBox(10);
            topBar.setStyle("-fx-padding: 10;");

            TextField searchField = new TextField();
            searchField.setPromptText("Search...");

            var masterData = table.getItems();
            FilteredList<UserResult> filteredData = new FilteredList<>(masterData, p -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(userResult -> {
                    if (newVal == null || newVal.isEmpty()) return true;

                    String lower = newVal.toLowerCase();

                    return userResult.getUserId().toLowerCase().contains(lower);
                });
            });

            Button export = new Button("Export to Excel");
            export.setStyle("-fx-padding: 8; -fx-background-color: #1D6F42; -fx-text-fill: #ffffff");
            export.setOnAction(e -> exportToExcel(table));
            HBox.setMargin(export, new Insets(0, 0, 0, 20));

            topBar.getChildren().add(searchField);
            topBar.getChildren().add(export);

            root.setTop(topBar);

            Scene scene = new Scene(root, 700, 450);
            scene.getStylesheets().add(getClass().getResource("/av/entrance/client/styles/table-style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Results");
            stage.setScene(scene);

            stage.getIcons().add(new Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

            stage.initModality(Modality.NONE);
            stage.show();
            stage.centerOnScreen();

            System.out.println(userResults);
        });

        resultsService.start();
    }

    private TableView<UserResult> createTable(ObservableList<UserResult> userResults) {
        TableView<UserResult> resultTable = new TableView<>(userResults);

        TableColumn<UserResult, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setStyle("-fx-alignment: CENTER");

        TableColumn<UserResult, Integer> attemptedCol = new TableColumn<>("Attempted");
        attemptedCol.setCellValueFactory(new PropertyValueFactory<>("numAttempted"));
        attemptedCol.setStyle("-fx-alignment: CENTER");

        TableColumn<UserResult, Integer> correctCol = new TableColumn<>("Correct");
        correctCol.setCellValueFactory(new PropertyValueFactory<>("numCorrect"));
        correctCol.setStyle("-fx-alignment: CENTER");

        TableColumn<UserResult, Integer> obtainedCol = new TableColumn<>("Obtained");
        obtainedCol.setCellValueFactory(new PropertyValueFactory<>("marksObtained"));
        obtainedCol.setSortType(TableColumn.SortType.DESCENDING);
        obtainedCol.setStyle("-fx-alignment: CENTER");

        resultTable.getColumns().addAll(userIdCol, attemptedCol, correctCol, obtainedCol);
        resultTable.getSortOrder().add(obtainedCol);

        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultTable.setTableMenuButtonVisible(false);
        resultTable.setSelectionModel(resultTable.getSelectionModel());
        resultTable.setPlaceholder(new Label("No Data Available"));
        resultTable.setEditable(false);

        resultTable.setStyle("-fx-table-cell-border-color: transparent;");

        return resultTable;
    }

    private void exportToExcel(TableView<UserResult> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Results of " + test.getTestName());

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumns().size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumns().get(i).getText());
            }

            for (int i = 0; i < table.getItems().size(); i++) {
                Row row = sheet.createRow(i + 1);
                UserResult result = table.getItems().get(i);

                row.createCell(0).setCellValue(result.getUserId());
                row.createCell(1).setCellValue(result.getNumAttempted());
                row.createCell(2).setCellValue(result.getNumCorrect());
                row.createCell(3).setCellValue(result.getMarksObtained());
            }

            for (int i = 0; i < table.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
