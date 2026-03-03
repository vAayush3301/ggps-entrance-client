package av.entrance.client.controller.user.items;

import av.entrance.client.controller.admin.NewTestController;
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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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
    public Button results;
    public Button editTestBtn;

    private Test test;

    private Server server;

    private ObservableList<Test> backingList;

    public void setData(Test test, ObservableList<Test> backingList) {
        this.test = test;
        this.backingList = backingList;
        testName.setText(test.getTestName());
        duration.setText(test.getDuration() + " Minute(s)");

        SVGPath editIcon, resultIcon, deleteIcon;
        editIcon = new SVGPath();
        resultIcon = new SVGPath();
        deleteIcon = new SVGPath();

        double iconSize = 24;

        Color iconColor = Color.valueOf("#fffed6");
        Group hostIcon = new Group();

        SVGPath p1 = new SVGPath();
        p1.setContent("M12 13v8 M9 21h6");
        p1.setStroke(iconColor);
        p1.setStrokeWidth(2);
        p1.setFill(null);

        SVGPath p2 = new SVGPath();
        p2.setContent("M11.95 12L12.05 12");
        p2.setStroke(iconColor);
        p2.setStrokeWidth(2.5);
        p2.setFill(null);

        SVGPath p3 = new SVGPath();
        p3.setContent("M5.64 18.36a9 9 0 1 1 12.72 0");
        p3.setStroke(iconColor);
        p3.setStrokeWidth(2);
        p3.setFill(null);

        SVGPath p4 = new SVGPath();
        p4.setContent("M15.54 15.54a5 5 0 1 0 -7.08 0");
        p4.setStroke(iconColor);
        p4.setStrokeWidth(2);
        p4.setFill(null);

        hostIcon.getChildren().addAll(p1, p2, p3, p4);

        editIcon.setContent("M20.8477 1.87868C19.6761 0.707109 17.7766 0.707105 16.605 1.87868L2.44744 16.0363C2.02864 16.4551 1.74317 16.9885 1.62702 17.5692L1.03995 20.5046C0.760062 21.904 1.9939 23.1379 3.39334 22.858L6.32868 22.2709C6.90945 22.1548 7.44285 21.8693 7.86165 21.4505L22.0192 7.29289C23.1908 6.12132 23.1908 4.22183 22.0192 3.05025L20.8477 1.87868ZM18.0192 3.29289C18.4098 2.90237 19.0429 2.90237 19.4335 3.29289L20.605 4.46447C20.9956 4.85499 20.9956 5.48815 20.605 5.87868L17.9334 8.55027L15.3477 5.96448L18.0192 3.29289ZM13.9334 7.3787L3.86165 17.4505C3.72205 17.5901 3.6269 17.7679 3.58818 17.9615L3.00111 20.8968L5.93645 20.3097C6.13004 20.271 6.30784 20.1759 6.44744 20.0363L16.5192 9.96448L13.9334 7.3787Z");
        editIcon.setFill(iconColor);

        deleteIcon.setContent("M0 281.296l0 -68.355q1.953 -37.107 29.295 -62.496t64.449 -25.389l93.744 0l0 -31.248q0 -39.06 27.342 -66.402t66.402 -27.342l312.48 0q39.06 0 66.402 27.342t27.342 66.402l0 31.248l93.744 0q37.107 0 64.449 25.389t29.295 62.496l0 68.355q0 25.389 -18.553 43.943t-43.943 18.553l0 531.216q0 52.731 -36.13 88.862t-88.862 36.13l-499.968 0q-52.731 0 -88.862 -36.13t-36.13 -88.862l0 -531.216q-25.389 0 -43.943 -18.553t-18.553 -43.943zm62.496 0l749.952 0l0 -62.496q0 -13.671 -8.789 -22.46t-22.46 -8.789l-687.456 0q-13.671 0 -22.46 8.789t-8.789 22.46l0 62.496zm62.496 593.712q0 25.389 18.553 43.943t43.943 18.553l499.968 0q25.389 0 43.943 -18.553t18.553 -43.943l0 -531.216l-624.96 0l0 531.216zm62.496 -31.248l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224zm31.248 -718.704l374.976 0l0 -31.248q0 -13.671 -8.789 -22.46t-22.46 -8.789l-312.48 0q-13.671 0 -22.46 8.789t-8.789 22.46l0 31.248zm124.992 718.704l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224zm156.24 0l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224z");
        deleteIcon.setFill(iconColor);

        resultIcon.setContent(
                "M455.241 19.689h-74.545c-4.645 0-8.409 3.764-8.409 8.409c0 4.645 3.764 8.409 8.409 8.409h74.545c22.023 0 39.939 17.916 39.939 39.938v359.107c0 22.022-17.916 39.938-39.939 39.938H335.334c-4.645 0-8.409 3.764-8.409 8.409s3.764 8.409 8.409 8.409h119.907c31.297 0 56.759-25.461 56.758-56.756V76.446C511.999 45.15 486.537 19.689 455.241 19.689z " +

                        "M301.697 475.491H56.758c-22.023 0-39.939-17.916-39.939-39.938V76.446c0-22.022 17.916-39.938 39.939-39.938H347.06c4.645 0 8.409-3.764 8.409-8.409c0-4.645-3.764-8.409-8.409-8.409H56.758C25.462 19.689 0 45.15 0 76.446v359.107c0 31.296 25.462 56.756 56.758 56.756h244.94c4.645 0 8.409-3.764 8.409-8.409S306.342 475.491 301.697 475.491z " +

                        "M447.441 61.651H72.583c-9.604 0-17.418 7.814-17.418 17.417v83.737c0 9.604 7.814 17.418 17.418 17.418h36.321c4.645 0 8.409-3.764 8.409-8.409c0-4.645-3.764-8.409-8.409-8.409H72.583c-0.325 0-0.6-0.275-0.6-0.6V79.068c0-0.325 0.275-0.599 0.6-0.599h374.859c0.325 0 0.6 0.274 0.6 0.599v83.735c0 0.325-0.275 0.6-0.6 0.6H142.54c-4.645 0-8.409 3.764-8.409 8.409s3.764 8.409 8.409 8.409h304.901c9.606 0 17.419-7.813 17.418-17.417V79.068C464.859 69.465 457.045 61.651 447.441 61.651z " +

                        "M228.892 208.704H63.573c-4.645 0-8.409 3.764-8.409 8.409v77.772c0 4.645 3.764 8.409 8.409 8.409h165.319c4.644 0 8.409-3.764 8.408-8.409v-77.772C237.301 212.468 233.537 208.704 228.892 208.704zM220.482 286.476h-148.5v-60.953h148.5V286.476z " +

                        "M228.892 340.621H63.573c-4.645 0-8.409 3.764-8.409 8.409v77.772c0 4.645 3.764 8.409 8.409 8.409h165.319c4.644 0 8.409-3.764 8.408-8.409V349.03C237.301 344.385 233.537 340.621 228.892 340.621zM220.482 418.393h-148.5V357.44h148.5V418.393z " +

                        "M447.441 208.704H282.122c-4.645 0-8.409 3.764-8.409 8.409v77.772c0 4.645 3.764 8.409 8.409 8.409h165.319c4.645 0 8.409-3.764 8.409-8.409v-77.772C455.85 212.468 452.086 208.704 447.441 208.704zM439.032 286.476H290.531v-60.953h148.501V286.476z " +

                        "M447.441 340.621H282.122c-4.645 0-8.409 3.764-8.409 8.409v77.772c0 4.645 3.764 8.409 8.409 8.409h165.319c4.645 0 8.409-3.764 8.409-8.409V349.03C455.85 344.385 452.086 340.621 447.441 340.621zM439.032 418.393H290.531V357.44h148.501V418.393z"
        );
        resultIcon.setFill(iconColor);
        resultIcon.setStroke(null);

        hostTest.setGraphic(fixedIcon(hostIcon, 20));
        editTestBtn.setGraphic(fixedIcon(editIcon, 20));
        results.setGraphic(fixedIcon(resultIcon, 20));
        deleteTest.setGraphic(fixedIcon(deleteIcon, 20));

        hostTest.setPrefSize(28, 28);
        editTestBtn.setPrefSize(28, 28);
        results.setPrefSize(28, 28);
        deleteTest.setPrefSize(28, 28);

        hostTest.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        editTestBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        results.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        deleteTest.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private Node fixedIcon(Node node, double size) {
        StackPane wrapper = new StackPane(node);
        wrapper.setPrefSize(size, size);
        wrapper.setMinSize(size, size);
        wrapper.setMaxSize(size, size);

        node.applyCss();

        Bounds b = node.getLayoutBounds();
        double scale = size / Math.max(b.getWidth(), b.getHeight());

        node.setScaleX(scale);
        node.setScaleY(scale);

        return wrapper;
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
            deleteTest.setStyle("-fx-background-color: #bb2121;");
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

    public void editTest() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/new_test.fxml"));
        Parent root = loader.load();

        NewTestController testController = loader.getController();
        testController.setTest(test);
        testController.editFlag = true;

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane);

        Stage stage = (Stage) testName.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Edit Test - Entrance Exam - Guru Gobind Singh Public School - Dhanbad");
        stage.setResizable(false);
        stage.show();
        stage.centerOnScreen();
    }
}
