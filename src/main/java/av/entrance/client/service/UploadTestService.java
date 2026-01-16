package av.entrance.client.service;

import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UploadTestService extends Service<String> {
    private final Test test;

    public UploadTestService(Test test) {
        this.test = test;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                String jsonBody = mapper.writeValueAsString(test);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://ggps-entrance.onrender.com/api/test/create"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println(response.body());

                return response.body();
            }
        };
    }
}
