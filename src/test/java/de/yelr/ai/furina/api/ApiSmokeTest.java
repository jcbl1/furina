package de.yelr.ai.furina.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Manual smoke test against a locally running instance. Disabled so the regular test suite stays
 * self-contained.
 */
class ApiSmokeTest {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String BASE_URL = "http://localhost:10240";

    private final OkHttpClient client = new OkHttpClient();

    @Test
    @Disabled("Requires the application, MySQL and Elasticsearch to be running")
    void healthEndpointIsPublic() throws IOException {
        Request request =
                new Request.Builder().url(BASE_URL + "/api/v1/system/health").get().build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("\"status\":\"UP\"");
        }
    }

    @Test
    @Disabled("Requires the application and a seeded database to be running")
    void loginReturnsToken() throws IOException {
        RequestBody body =
                RequestBody.create(JSON, "{\"username\":\"admin\",\"password\":\"password\"}");
        Request request =
                new Request.Builder().url(BASE_URL + "/api/v1/auth/login").post(body).build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("\"token\"");
        }
    }
}
