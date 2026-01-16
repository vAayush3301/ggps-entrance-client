package av.entrance.client.model;

import java.util.List;

public class Test {

    private String testName;
    private List<Question> questions;

    public Test(String testName, List<Question> questions) {
        this.testName = testName;
        this.questions = questions;
    }

    public Test() {
    }

    public String getTestName() {
        return testName;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
