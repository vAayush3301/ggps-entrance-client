package av.entrance.client.server;

import av.entrance.client.model.SubmitResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LocalStore {
    public static String testName = "NO_TEST_NAME";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Path FILE = getStoragePath();

    private static Path getStoragePath() {
        String appData = System.getenv("APPDATA");

        if (appData == null) {
            appData = System.getProperty("user.home");
        }

        return Paths.get(appData, "ExamDesk", "data", testName + ".jsonl");
    }

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
            throw new RuntimeException("Could not initialize local storage in AppData", e);
        }
    }

    public static synchronized void append(SubmitResponse payload) throws IOException {
        Files.createDirectories(FILE.getParent());
        if (!Files.exists(FILE)) Files.createFile(FILE);

        String json = mapper.writeValueAsString(payload);
        Files.writeString(FILE, json + "\n", StandardOpenOption.APPEND);
    }

    public static List<SubmitResponse> readAll() throws IOException {
        if (!Files.exists(FILE)) return List.of();

        return Files.lines(FILE).filter(l -> !l.isBlank()).map(l -> {
            try {
                return mapper.readValue(l, SubmitResponse.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public static void clear() throws IOException {
        if (Files.exists(FILE)) {
            Files.writeString(FILE, "");
        }
    }
}