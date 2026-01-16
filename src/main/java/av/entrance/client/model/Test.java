package av.entrance.client.model;

import java.util.List;

public class Test {
    private String testId;
    private String testName;
    private List<Question> questions;

    public Test(String testId, String testName, List<Question> questions) {
        this.testId = testId;
        this.testName = testName;
        this.questions = questions;
    }

    public Test(String testName, List<Question> questions) {
        this.testName = testName;
        this.questions = questions;
    }

    public Test() {
    }

    public String getTestId() {
        return testId;
    }

    public String getTestName() {
        return testName;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
