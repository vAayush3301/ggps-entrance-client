package av.entrance.client.server;

import av.entrance.client.model.Response;
import av.entrance.client.model.Test;

import java.util.List;

public class Payload {
    private Test test;
    private List<Response> responses;

    private String userId;

    public Payload(String userId, List<Response> responses) {
        this.userId = userId;
        this.responses = responses;
    }

    public Payload(Test test) {
        this.test = test;
    }

    public Payload() {
    }

    public Test getTest() {
        return test;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public String getUserId() {
        return userId;
    }
}
