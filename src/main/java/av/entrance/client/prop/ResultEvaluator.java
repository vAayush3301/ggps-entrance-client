package av.entrance.client.prop;

import av.entrance.client.model.Response;
import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.UserResult;

import java.util.ArrayList;
import java.util.List;

public class ResultEvaluator {
    private List<SubmitResponse> responses;

    public ResultEvaluator(List<SubmitResponse> responses) {
        this.responses = responses;
    }

    public UserResult getUserResult(String userId) {
        UserResult result = new UserResult();
        result.setUserId(userId);

        for (SubmitResponse response : responses) {
            if (response.userId == userId) {
                List<Response> userResponses = response.responses;
                result.setNumAttempted(responses.size());

                for (Response qResponse : userResponses) {
                    if (String.valueOf(qResponse.getResponseCode()).equals(qResponse.getQuestion().getCorrectOption())) {
                        result.marksObtained += 4;
                        result.numCorrect++;
                    } else result.marksObtained--;
                }

                break;
            }
        }

        return result;
    }

    public List<UserResult> getTotalResult() {
        List<UserResult> userResults = new ArrayList<>();

        for (SubmitResponse response : responses) {
            userResults.add(getUserResult(response.userId));
        }

        return userResults;
    }
}
