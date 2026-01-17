package av.entrance.client.service;

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

public class DownloadTestService extends Service<List<Test>> {
    @Override
    protected Task<List<Test>> createTask() {
        return new Task<>() {
            @Override
            protected List<Test> call() throws IOException, InterruptedException {

                try {
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://ggps-entrance.onrender.com/api/test/get_tests"))
                            .timeout(Duration.ofSeconds(40))
                            .GET()
                            .build();

                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    ObjectMapper mapper = new ObjectMapper();

                    Test[] testsArray = mapper.readValue(response.body(), Test[].class);
                    System.out.println(testsArray);

                    return List.of(testsArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }
}
