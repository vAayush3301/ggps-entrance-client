package av.entrance.client.controller.admin;

import av.entrance.client.model.Question;
import av.entrance.client.model.Test;
import av.entrance.client.service.UploadTestService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NewTestController {
    public Label questionResponse;
    public HBox qNoBox;
    public Label testName;
    public TextField testNameEdit;
    @FXML
    private TextField o1, o2, o3, o4, co;
    @FXML
    private TextField questionField;
    @FXML
    private Label questionCount;

    private List<Question> questions = new ArrayList<>();
    private HashMap<Integer, Button> questionButtons = new HashMap<>();
    private int currentQuestion = 1;

    @FXML
    public void initialize() {
        testName.textProperty().bind(testNameEdit.textProperty());
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
    }

    public void publish() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Host Test");
        dialog.setHeaderText("Enter Port");
        dialog.setContentText("Port:");

        Optional<String> result = dialog.showAndWait();
        String duration = "60";
        if (result.isPresent()) {
            duration = result.get();
        }

        Test test;
        try {
            test = new Test(testName.getText(), questions, Integer.parseInt(duration));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Time Not Specified");
            alert.setContentText("Test Duration must be numeric.");

            alert.showAndWait();

            return;
        }

        UploadTestService service = new UploadTestService(test);
        service.setOnSucceeded(e -> {
            String response = service.getValue();
            System.out.println("Server response: " + response);

            questionResponse.setText("Test Published");
        });

        service.setOnFailed(e -> {
            Throwable ex = service.getException();
            ex.printStackTrace();

            questionResponse.setText("Failed to publish Test");
        });

        service.start();
    }
}
