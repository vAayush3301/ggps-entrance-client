package av.entrance.client.service;

import av.entrance.client.model.Image;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteImageService extends Service<String> {
    private String imageKey;

    public DeleteImageService(String imageKey) {
        this.imageKey = imageKey;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://ggps-entrance-xpiz.onrender.com/api/image/deleteImage?key=" + imageKey))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                return response.body();
            }
        };
    }
}
