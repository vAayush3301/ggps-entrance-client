package av.entrance.client.controller.admin;

import av.entrance.client.model.Image;
import av.entrance.client.model.Question;
import av.entrance.client.model.Test;
import av.entrance.client.service.DeleteImageService;
import av.entrance.client.service.DeleteTestService;
import av.entrance.client.service.ImageUploadService;
import av.entrance.client.service.UploadTestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NewTestController {
    private final HashMap<Integer, Button> questionButtons = new HashMap<>();
    private final List<Image> imageKeys = new ArrayList<>();
    public Label questionResponse;
    public HBox qNoBox;
    public Label testName;
    public TextField testNameEdit;
    public boolean editFlag = false;
    public Button addImages;
    @FXML
    private TextField o1, o2, o3, o4, co;
    @FXML
    private TextField questionField;
    @FXML
    private Label questionCount;
    private List<Question> questions = new ArrayList<>();
    private int currentQuestion = 1;
    private Test test;

    @FXML
    public void initialize() {
        testName.textProperty().bind(testNameEdit.textProperty());

        SVGPath addImagesPath = new SVGPath();
        addImagesPath.setContent("M23 4v2h-3v3h-2V6h-3V4h3V1h2v3h3zm-8.5 7c.828 0 1.5-.672 1.5-1.5S15.328 8 14.5 8 13 8.672 13 9.5s.672 1.5 1.5 1.5zm3.5 3.234l-.513-.57c-.794-.885-2.18-.885-2.976 0l-.655.73L9 9l-3 3.333V6h7V4H6c-1.105 0-2 .895-2 2v12c0 1.105.895 2 2 2h12c1.105 0 2-.895 2-2v-7h-2v3.234z");
        addImagesPath.setFill(Color.WHITE);
        addImages.setGraphic(addImagesPath);
    }

    public void setTest(Test test) {
        this.test = test;
        questions = test.getQuestions();
        testNameEdit.setText(test.getTestName());
        loadQuestion(1);
    }

    public void handleNext() {
        questionResponse.setText("");

        if (!saveQuestion()) return;

        currentQuestion++;
        loadQuestion(currentQuestion);
    }

    private boolean saveQuestion() {
        String qText = questionField.getText();
        String option1 = o1.getText();
        String option2 = o2.getText();
        String option3 = o3.getText();
        String option4 = o4.getText();
        String correct = co.getText();

        if (qText.isEmpty() || option1.isEmpty() || option2.isEmpty() || correct.isEmpty()) {
            questionResponse.setText("Fill all mandatory fields!");
            return false;
        }

        if (!correct.matches("^[1-4]$")) {
            questionResponse.setText("Please enter option code only!");
            return false;
        }

        if (currentQuestion - 1 == questions.size()) {
            questions.add(new Question(qText, option1, option2, option3, option4, correct));
        } else {
            questions.set(currentQuestion - 1, new Question(qText, option1, option2, option3, option4, correct));
        }


        if (questionButtons.get(currentQuestion) == null) {
            Button qButton = new Button(String.valueOf(currentQuestion));
            qButton.getStyleClass().add(".question-btn");
            qButton.applyCss();
            qButton.layout();
            qNoBox.getChildren().add(qButton);
            qButton.setOnAction(e -> {
                int key = Integer.parseInt(qButton.getText());
                loadQuestion(key);
            });
            questionButtons.put(currentQuestion, qButton);
        }

        return true;
    }

    private void loadQuestion(int key) {
        currentQuestion = key;
        questionResponse.setText("");

        if (key > questions.size()) {
            questionField.clear();
            o1.clear();
            o2.clear();
            o3.clear();
            o4.clear();
            co.clear();

            questionCount.setText(key + ".");
            return;
        }

        Question question = questions.get(currentQuestion - 1);

        String qText = question.getQuestionText();
        String option1 = question.getOption1();
        String option2 = question.getOption2();
        String option3 = question.getOption3();
        String option4 = question.getOption4();
        String correct = question.getCorrectOption();

        questionField.setText(qText);
        o1.setText(option1);
        o2.setText(option2);
        o3.setText(option3);
        o4.setText(option4);
        co.setText(correct);

        questionCount.setText(currentQuestion + ".");
    }

    public void handlePrevious() {
        if (currentQuestion == 1) {
            questionResponse.setText("This is the first question");
            return;
        }
        currentQuestion--;
        questionResponse.setText("");

        loadQuestion(currentQuestion);
    }

    public void cancel() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/dashboard.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) questionResponse.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Admin Dashboard");
        stage.show();
        stage.centerOnScreen();
    }

    public void publish() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Publish Test");
        dialog.setHeaderText("Test Duration");
        dialog.setContentText("Enter Test duration in minutes:");

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

        Optional<String> result = dialog.showAndWait();
        String duration;
        if (result.isPresent()) {
            duration = result.get();
        } else return;

        Test test;
        try {
            test = new Test(testName.getText(), questions, Integer.parseInt(duration));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Time Not Specified");
            alert.setContentText("Test Duration must be numeric.");

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

            alert.showAndWait();

            return;
        }

        UploadTestService service = new UploadTestService(test);
        service.setOnSucceeded(e -> {
            String response = service.getValue();
            System.out.println("Server response: " + response);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Test has been created");
            alert.setContentText("You will be redirected to Home Page.");

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

            ButtonType okType = new ButtonType("OK");
            alert.getButtonTypes().setAll(okType);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/av/entrance/client/admin/dashboard.fxml"));
            DashboardController controller = loader.getController();

            Button okButton = (Button) alert.getDialogPane().lookupButton(okType);
            okButton.setOnAction(actionEvent -> {
                Parent root;
                try {
                    root = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                Stage stage1 = (Stage) questionResponse.getScene().getWindow();
                stage1.setScene(new Scene(root));
                stage1.setTitle("Admin Dashboard");
                stage1.show();
                stage1.centerOnScreen();
            });

            alert.showAndWait();

            questionResponse.setText("Test Published");

            if (editFlag) {
                DeleteTestService deleteTestService = new DeleteTestService(this.test);
                deleteTestService.setOnSucceeded(event -> {
                    String deleteResponse = deleteTestService.getValue();
                    System.out.println("Server response: " + deleteResponse);
                });

                deleteTestService.start();
            }
            controller.refresh();
        });

        service.setOnFailed(e -> {
            Throwable ex = service.getException();
            ex.printStackTrace();

            questionResponse.setText("Failed to publish Test");
        });

        service.start();
    }

    public void addImage() {
        Stage stage = new Stage();
        TableView<Image> imageTable = createTable(imageKeys);

        BorderPane root = new BorderPane();
        root.setCenter(imageTable);

        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10;");

        TextField imageText = new TextField();
        imageText.setPromptText("Image Text");

        Button export = new Button("Upload Image");
        export.setStyle("-fx-padding: 8; -fx-background-color: #1D6F42; -fx-text-fill: #ffffff");
        export.setOnAction(e -> uploadImage(stage, imageText.getText(), imageTable));
        HBox.setMargin(export, new Insets(0, 0, 0, 20));

        topBar.getChildren().add(imageText);
        topBar.getChildren().add(export);
        root.setTop(topBar);

        Scene scene = new Scene(root, 700, 450);
        scene.getStylesheets().add(getClass().getResource("/av/entrance/client/styles/table-style.css").toExternalForm());

        stage.setTitle("Images");
        stage.setScene(scene);

        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/av/entrance/client/images/logos/logo.png")));

        stage.initModality(Modality.NONE);
        stage.show();
        stage.centerOnScreen();
    }

    private void uploadImage(Stage stage, String altText, TableView<Image> imageTable) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fileChooser.showSaveDialog(stage);

        ImageUploadService uploadService = new ImageUploadService(selected);

        uploadService.setOnSucceeded(event -> {
            String key = uploadService.getValue();

            if (!key.isEmpty()) {
                Image image = new Image(key, altText);
                imageKeys.add(image);
                imageTable.setItems(FXCollections.observableArrayList(imageKeys));
            }
        });

        uploadService.start();
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

    private TableView<Image> createTable(List<Image> images) {
        ObservableList<Image> imageKeys = FXCollections.observableArrayList(images);
        TableView<Image> imageTable = new TableView<>(imageKeys);

        TableColumn<Image, String> altTextCol = new TableColumn<>("Text");
        altTextCol.setCellValueFactory(new PropertyValueFactory<>("imageAlt"));
        altTextCol.setStyle("-fx-alignment: CENTER");

        TableColumn<Image, String> keyCol = new TableColumn<>("Key");
        keyCol.setCellValueFactory(new PropertyValueFactory<>("imageKey"));
        keyCol.setStyle("-fx-alignment: CENTER");

        TableColumn<Image, Void> actionCol = new TableColumn<>("Delete");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final SVGPath deleteIcon = new SVGPath();
            private final Button delete = new Button("Delete");

            {
                delete.setStyle("-fx-background-color: #bb2121;");
                deleteIcon.setContent("M0 281.296l0 -68.355q1.953 -37.107 29.295 -62.496t64.449 -25.389l93.744 0l0 -31.248q0 -39.06 27.342 -66.402t66.402 -27.342l312.48 0q39.06 0 66.402 27.342t27.342 66.402l0 31.248l93.744 0q37.107 0 64.449 25.389t29.295 62.496l0 68.355q0 25.389 -18.553 43.943t-43.943 18.553l0 531.216q0 52.731 -36.13 88.862t-88.862 36.13l-499.968 0q-52.731 0 -88.862 -36.13t-36.13 -88.862l0 -531.216q-25.389 0 -43.943 -18.553t-18.553 -43.943zm62.496 0l749.952 0l0 -62.496q0 -13.671 -8.789 -22.46t-22.46 -8.789l-687.456 0q-13.671 0 -22.46 8.789t-8.789 22.46l0 62.496zm62.496 593.712q0 25.389 18.553 43.943t43.943 18.553l499.968 0q25.389 0 43.943 -18.553t18.553 -43.943l0 -531.216l-624.96 0l0 531.216zm62.496 -31.248l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224zm31.248 -718.704l374.976 0l0 -31.248q0 -13.671 -8.789 -22.46t-22.46 -8.789l-312.48 0q-13.671 0 -22.46 8.789t-8.789 22.46l0 31.248zm124.992 718.704l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224zm156.24 0l0 -406.224q0 -13.671 8.789 -22.46t22.46 -8.789l62.496 0q13.671 0 22.46 8.789t8.789 22.46l0 406.224q0 13.671 -8.789 22.46t-22.46 8.789l-62.496 0q-13.671 0 -22.46 -8.789t-8.789 -22.46zm31.248 0l62.496 0l0 -406.224l-62.496 0l0 406.224z");
                deleteIcon.setFill(Color.valueOf("#fffed6"));
                delete.setGraphic(fixedIcon(deleteIcon, 20));
                delete.setPrefSize(28, 28);
                delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                delete.setOnAction(event -> {
                    Image imageDel = getTableView().getItems().get(getIndex());

                    DeleteImageService deleteImageService = new DeleteImageService(imageDel.getImageKey());
                    deleteImageService.setOnSucceeded(event1 -> getTableView().getItems().remove(imageDel));
                    deleteImageService.start();
                });
            }

            @Override
            protected void updateItem(Void unused, boolean b) {
                super.updateItem(unused, b);
                setGraphic(b ? null : delete);
            }
        });

        imageTable.getColumns().addAll(altTextCol, keyCol, actionCol);

        imageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        imageTable.setTableMenuButtonVisible(false);
        imageTable.setSelectionModel(imageTable.getSelectionModel());
        imageTable.setPlaceholder(new Label("No Data Available"));
        imageTable.setEditable(false);

        imageTable.setStyle("-fx-table-cell-border-color: transparent;");

        return imageTable;
    }
}
