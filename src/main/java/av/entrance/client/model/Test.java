package av.entrance.client.model;

import java.util.List;

public class Test {
    private String testId;
    private String testName;
    private List<Question> questions;
    private int duration;

    public Test(String testId, String testName, List<Question> questions, int duration) {
        this.testId = testId;
        this.testName = testName;
        this.questions = questions;
        this.duration = duration;
    }

    public Test(String testName, List<Question> questions, int duration) {
        this.testName = testName;
        this.questions = questions;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
