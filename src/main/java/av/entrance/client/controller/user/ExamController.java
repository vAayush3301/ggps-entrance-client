package av.entrance.client.controller.user;

import av.entrance.client.model.Question;
import av.entrance.client.model.Response;
import av.entrance.client.model.Test;
import av.entrance.client.service.DownloadTestService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class ExamController {
    public Label testName;
    private List<Question> questions = new ArrayList<>();
    private final List<Response> responses = new ArrayList<>();
    public RadioButton o1, o2, o3, o4;
    public Label questionCount;
    public Label questionText;
    public Label questionResponse;
    private ToggleGroup optionGroup;
    private int currentCount = 1;

    @FXML
    public void initialize() {
        DownloadTestService downloadTest = new DownloadTestService();
        downloadTest.setOnSucceeded(event -> {
            List<Test> tests = downloadTest.getValue();
            testName.setText(tests.get(0).getTestName());

            questions = tests.get(0).getQuestions();
            loadQuestion(currentCount);
        });

        downloadTest.start();

        optionGroup = new ToggleGroup();

        o1.setToggleGroup(optionGroup);
        o2.setToggleGroup(optionGroup);
        o3.setToggleGroup(optionGroup);
        o4.setToggleGroup(optionGroup);

        questionCount.setText(currentCount + ".");
    }

    public void submit() {
    }

    public void handlePrevious() {
        if (currentCount == 1) {
            questionResponse.setText("This is the first question!");
            return;
        }

        saveResponse(currentCount);
        currentCount--;
        loadQuestion(currentCount);
        clearSelection();
    }

    public void handleNext() {
        if (currentCount == questions.size()) {
            questionResponse.setText("This is the last question!");
            return;
        }

        saveResponse(currentCount);
        currentCount++;
        loadQuestion(currentCount);
        clearSelection();
    }

    private void loadQuestion(int key) {
        currentCount = key;
        questionResponse.setText("");

        if (key > questions.size() || key <= 0) {
            return;
        }

        Question question = questions.get(currentCount - 1);

        String qText = question.getQuestionText();
        String option1 = question.getOption1();
        String option2 = question.getOption2();
        String option3 = question.getOption3();
        String option4 = question.getOption4();

        questionText.setText(qText);
        o1.setText(option1);
        o2.setText(option2);
        o3.setText(option3);
        o4.setText(option4);

        questionCount.setText(currentCount + ".");
    }

    private void saveResponse(int key) {
        Toggle selected = optionGroup.getSelectedToggle();
        if (selected == null) return;

        RadioButton optionButton = (RadioButton) selected;

        Response response = new Response(questions.get(key), getOptionCode(questions.get(key), optionButton));
        responses.add(response);
    }

    private int getOptionCode(Question question, RadioButton optionButton) {
        String o1 = question.getOption1();
        String o2 = question.getOption2();
        String o3 = question.getOption3();
        String o4 = question.getOption4();

        String text = optionButton.getText();
        if (text.equals(o1)) {
            return 1;
        } else if (text.equals(o2)) {
            return 2;
        } else if (text.equals(o3)) {
            return 3;
        } else if (text.equals(o4)) {
            return 4;
        }

        return 0;
    }

    public void saveTest() {
    }

    public void clearSelection() {
        optionGroup.selectToggle(null);
    }
}
