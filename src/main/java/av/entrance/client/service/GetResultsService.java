package av.entrance.client.service;

import av.entrance.client.Client;
import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class GetResultsService extends Service<List<SubmitResponse>> {
    private String testId;

    public GetResultsService(String testId) {
        this.testId = testId;
    }

    @Override
    protected Task<List<SubmitResponse>> createTask() {
        return new Task<>() {
            @Override
            protected List<SubmitResponse> call() throws IOException, InterruptedException {

                try {
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://ggps-entrance-xpiz.onrender.com/api/test/get_results?testId=" + testId + "&clientId=" + Client.CLIENT_ID))
                            .timeout(Duration.ofSeconds(40))
                            .GET()
                            .build();

                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    ObjectMapper mapper = new ObjectMapper();

                    SubmitResponse[] responsesArray = mapper.readValue(response.body(), SubmitResponse[].class);
                    System.out.println(responsesArray);

                    return List.of(responsesArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }
}
