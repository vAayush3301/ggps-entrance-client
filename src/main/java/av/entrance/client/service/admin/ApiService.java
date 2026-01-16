package av.entrance.client.service.admin;

import av.entrance.client.model.Test;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService extends Service<String> {
    private final Test test;

    public ApiService(Test test) {
        this.test = test;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(test);
                oos.flush();
                byte[] payload = baos.toByteArray();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://ggps-entrance.onrender.com/api/test/create"))
                        .header("Content-Type", "application/x-java-serialized-object")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

                return response.body();
            }
        };
    }
}
