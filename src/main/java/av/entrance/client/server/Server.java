package av.entrance.client.server;

import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;

public class Server {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final int port;
    private final Test test;
    private HttpServer server;
    private boolean isStarted = false;

    public Server(int port, Test test) {
        this.port = port;
        this.test = test;
    }

    private static void forwardToHost() throws Exception {
        List<SubmitResponse> responses = LocalStore.readAll();
        if (responses.isEmpty()) return;

        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL("https://ggps-entrance-xpiz.onrender.com/api/test/submitResponse");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        mapper.writeValue(conn.getOutputStream(), responses);

        if (conn.getResponseCode() == 200) {
            LocalStore.clear();
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        isStarted = true;

        server.createContext("/api/getTest", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

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
