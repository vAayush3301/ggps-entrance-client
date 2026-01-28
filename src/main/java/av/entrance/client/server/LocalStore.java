package av.entrance.client.server;

import av.entrance.client.model.SubmitResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LocalStore {
    private static final Path FILE = Path.of("av/entrance/client/data/submissions.jsonl");
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        try {
            Path parent = FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void append(SubmitResponse payload) throws IOException {
        String json = mapper.writeValueAsString(payload);
        Files.writeString(FILE, json + "\n", StandardOpenOption.APPEND);
    }

    public static List<SubmitResponse> readAll() throws IOException {
        return Files.lines(FILE).filter(l -> !l.isBlank()).map(l -> {
            try {
                return mapper.readValue(l, SubmitResponse.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public static void clear() throws IOException {
        Files.writeString(FILE, "");
    }
}