package io.github.martinez1337.hmac.http.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

public abstract class BaseHttpHandlerTest {

    @Mock protected HttpExchange exchange;
    @Mock protected Headers requestHeaders;
    protected Headers responseHeaders;
    protected ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUpBase() {
        responseHeaders = new Headers();
        outputStream = new ByteArrayOutputStream();

        lenient().when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        lenient().when(exchange.getResponseBody()).thenReturn(outputStream);
        lenient().when(exchange.getRequestHeaders()).thenReturn(requestHeaders);
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
}
