package av.entrance.client.server;

import av.entrance.client.model.Image;
import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static LocalStore localStore;
    private final int port;
    private final Test test;
    private HttpServer server;
    private boolean isStarted = false;

    public Server(int port, Test test) {
        this.port = port;
        this.test = test;

        localStore = new LocalStore(test.getTestName());
    }

    private static void forwardToHost() throws Exception {
        List<SubmitResponse> responses = localStore.readAll();
        if (responses.isEmpty()) return;

        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL("https://ggps-entrance-xpiz.onrender.com/api/test/submitResponse");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        mapper.writeValue(conn.getOutputStream(), responses);

        if (conn.getResponseCode() == 200) {
            localStore.clear();
        }
    }

    public void start() throws IOException, InterruptedException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        isStarted = true;

        List<Image> localImages = new ArrayList<>();

        String appData = System.getenv("APPDATA");
        if (appData == null) appData = System.getProperty("user.home") + "/ExamDesk";
        File tempDir = new File(appData, "ExamDesk/images/" + test.getTestName());
        tempDir.mkdirs();

        for (Image image : test.getImageKeys()) {
            String encodedKey = URLEncoder.encode(image.getImageKey(), StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ggps-entrance-xpiz.onrender.com/api/image?key=" + encodedKey))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            String imageUrl = response.body();

            String extension = "png";
            int lastDot = imageUrl.lastIndexOf('.');
            if (lastDot != -1 && lastDot < imageUrl.length() - 1) {
                extension = imageUrl.substring(lastDot + 1).split("\\?|#")[0].toLowerCase();
            }

            String fileName = "image_" + System.currentTimeMillis() + "." + extension;
            File file = new File(tempDir, fileName);

            HttpClient.newHttpClient()
                    .sendAsync(
                            HttpRequest.newBuilder().uri(URI.create(imageUrl)).build(),
                            HttpResponse.BodyHandlers.ofFile(file.toPath())
                    )
                    .thenAccept(pathHttpResponse -> {
                                System.out.println("Saved to: " + file.getAbsolutePath());
                                localImages.add(new Image(fileName, image.getImageAlt()));
                            }
                    );
        }

        server.createContext("/images", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String fileName = path.substring("/images/".length());
            File f = new File(tempDir, fileName);

            String extension = "png";
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot != -1 && lastDot < fileName.length() - 1) {
                extension = fileName.substring(lastDot + 1).split("\\?|#")[0].toLowerCase();
            }

            if (!f.exists()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "image/" + extension);
            exchange.sendResponseHeaders(200, f.length());

            try (var os = exchange.getResponseBody(); var fis = new FileInputStream(f)) {
                fis.transferTo(os);
            }
        });

        server.createContext("/api/getTest", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            test.setImageKeys(localImages);
            Payload payload = new Payload(test);
            byte[] data = mapper.writeValueAsBytes(payload);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.close();
        });

        server.createContext("/ingest", new IngestHandler(test));

        server.setExecutor(null);
        server.start();

        System.out.println("Test hosted on port " + port);
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop(1);
            isStarted = false;
            forwardToHost();
            System.out.println("Test ended");
        }
    }

    public boolean isStarted() {
        return isStarted;
    }
}
