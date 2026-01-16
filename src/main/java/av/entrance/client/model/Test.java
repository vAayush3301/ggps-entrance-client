package av.entrance.client.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Test implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String testName;
    private List<Question> questions;

    public Test(String testName, List<Question> questions) {
        this.testName = testName;
        this.questions = questions;
    }

    public String getTestName() {
        return testName;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
