package av.entrance.client.server;

import av.entrance.client.model.SubmitResponse;
import av.entrance.client.model.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IngestHandler implements HttpHandler {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Test test;

    public IngestHandler(Test test) {
        this.test = test;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("INGEST HIT!!!");

        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Payload payload = mapper.readValue(exchange.getRequestBody(), Payload.class);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = LocalDate.now().format(fmt).replace("-", "_");

            SubmitResponse submitResponse = new SubmitResponse(
                    test.getTestId(),
                    payload.getUserId(),
                    date,
                    payload.getResponses()
            );

            LocalStore.append(submitResponse);

            byte[] response = "OK".getBytes();

            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            exchange.getResponseBody().close();
            exchange.close();
        }
    }
}
