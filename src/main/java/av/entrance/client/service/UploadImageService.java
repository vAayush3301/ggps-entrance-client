package av.entrance.client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import okhttp3.*;

import java.io.File;
import java.util.Objects;

public class UploadImageService extends Service<String> {
    private final File image;

    public UploadImageService(File image) {
        this.image = image;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                OkHttpClient client = new OkHttpClient();

                RequestBody fileBody = RequestBody.create(
                        image,
                        MediaType.parse("image/*")
                );

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                                "file",
                                image.getName(),
                                fileBody
                        )
                        .build();

                Request request = new Request.Builder()
                        .url("https://ggps-entrance-xpiz.onrender.com/api/image/upload")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    return Objects.requireNonNull(response.body()).string();
                }
            }
        };
    }
}
