package av.entrance.client.server;

import av.entrance.client.model.Response;
import av.entrance.client.model.Test;

import java.util.List;

public class Payload {
    private Test test;
    private String type;
    private List<Response> responses;

    private String userId;

    public Payload(String userId, List<Response> responses, String type) {
        this.userId = userId;
        this.responses = responses;
        this.type = type;
    }

    public Payload(Test test, String type) {
        this.test = test;
        this.type = type;
    }

    public Payload() {
    }
}
