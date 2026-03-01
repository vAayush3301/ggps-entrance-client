package av.entrance.client.model;

public class UserResult {
    private String userId;
    public int marksObtained, numCorrect;
    private int numAttempted;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNumAttempted(int numAttempted) {
        this.numAttempted = numAttempted;
    }
}
