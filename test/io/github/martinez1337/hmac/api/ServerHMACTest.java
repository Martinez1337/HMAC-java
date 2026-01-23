package io.github.martinez1337.hmac.api;

import io.github.martinez1337.hmac.config.AppConfig;
import org.junit.jupiter.api.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ServerHMACTest {

    private static ServerHMAC server;
    private static AppConfig appConfig;
    private static final int TEST_PORT = 9999;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    static void setup() throws IOException {
        appConfig = mock(AppConfig.class);

        byte[] keyBytes = new byte[32];
        Key testKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        when(appConfig.getPort()).thenReturn(TEST_PORT);
        when(appConfig.getAlgorithm()).thenReturn("HmacSHA256");
        when(appConfig.getHmacKey()).thenReturn(testKey);
        when(appConfig.getMaxMsgSizeBytes()).thenReturn(4096L);
        when(appConfig.getMaxPayloadSize()).thenReturn(8192L);

        server = new ServerHMAC(appConfig);
        server.start();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void signEndpoint_shouldReturnOk() throws IOException, InterruptedException {
        String jsonBody = "{\"msg\": \"test-message\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/sign"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Сервер должен успешно подписывать сообщение"+ response.body());
    }

    @Test
    void unknownEndpoint_shouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/unknown"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}