package av.entrance.client.server;

import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final int port;
    private Test test;
    private HttpServer server;

    public Server(int port, Test test) {
        this.port = port;
        this.test = test;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/getTest", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Payload payload = new Payload(test, "TEST");
            byte[] data = mapper.writeValueAsBytes(payload);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.close();
        });

        server.createContext("/api/submitResponse", exchange -> {
            Payload response = mapper.readValue(exchange.getRequestBody(), Payload.class);
            System.out.println("Received: " + response);
            exchange.close();
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Test hosted on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(2);
            System.out.println("Test ended");
        }
    }
}
