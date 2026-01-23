package io.github.martinez1337.hmac.api.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public abstract class BaseHttpHandlerTest {
    protected static final String BASE_CONTENT_TYPE = "application/json";

    @Mock protected AppConfig appConfig;
    @Mock protected HttpExchange exchange;
    @Mock protected Headers requestHeaders;

    protected Headers responseHeaders;
    protected ByteArrayOutputStream outputStream;
    protected Gson gson;
    protected BaseHttpHandler handler;

    @BeforeEach
    void setUpBase() {
        responseHeaders = new Headers();
        outputStream = new ByteArrayOutputStream();
        gson = new GsonBuilder().create();

        lenient().when(appConfig.getMaxPayloadSize()).thenReturn(8192L);
        lenient().when(appConfig.getMaxMsgSizeBytes()).thenReturn(4096L);
        lenient().when(appConfig.getSigMaxSizeBytes()).thenReturn(44L);

        lenient().when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        lenient().when(exchange.getResponseBody()).thenReturn(outputStream);
        lenient().when(exchange.getRequestHeaders()).thenReturn(requestHeaders);
        lenient().when(exchange.getRequestURI()).thenReturn(URI.create("/test"));
    }

    protected void setupRequest(String method, String contentType, String body) {
        lenient().when(exchange.getRequestMethod()).thenReturn(method);
        lenient().when(requestHeaders.getFirst("Content-Type")).thenReturn(contentType);

        if (body != null) {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            lenient().when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(bytes));
            lenient().when(requestHeaders.getFirst("Content-Length")).thenReturn(String.valueOf(bytes.length));
        }
    }

    protected void assertResponse(int statusCode, String expectedBodyPart) throws IOException {
        verify(exchange).sendResponseHeaders(eq(statusCode), anyLong());
        if (expectedBodyPart != null) {
            String actualBody = outputStream.toString(StandardCharsets.UTF_8).replaceAll("\\s+", "");
            String expected = expectedBodyPart.replaceAll("\\s+", "");
            assertTrue(actualBody.contains(expected),
                    () -> "Expected body to contain: " + expectedBodyPart + ", but got: " + actualBody);
        }
    }

    @Test
    void handle_payloadTooLarge_sendsPayloadTooLarge() throws IOException {
        setupRequest("POST", BASE_CONTENT_TYPE, null);
        when(requestHeaders.getFirst("Content-Length")).thenReturn("10000"); // Больше лимита 8192

        handler.handle(exchange);

        assertResponse(413, "payload_too_large");
    }

    @Test
    void handle_invalidContentType_sendsUnsupportedMediaType() throws IOException {
        setupRequest("POST", "text/plain", "just text");

        handler.handle(exchange);

        assertResponse(415, "unsupported_media_type");
    }

    @Test
    void handle_invalidJson_sendsInvalidJson() throws IOException {
        setupRequest("POST", BASE_CONTENT_TYPE, "{invalid}");

        handler.handle(exchange);

        assertResponse(400, "invalid_json");
    }
}
