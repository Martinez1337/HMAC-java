package io.github.martinez1337.hmac.http.handler;

import io.github.martinez1337.hmac.api.handler.SignatureHandler;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.service.SignatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignatureHandlerTest extends BaseHttpHandlerTest {

    @Mock private SignatureService signatureService;
    @Mock private AppConfig appConfig;
    private SignatureHandler handler;

    @BeforeEach
    void setUp() {
        lenient().when(appConfig.getMaxPayloadSize()).thenReturn(8192L);
        lenient().when(appConfig.getMaxMsgSizeBytes()).thenReturn(4096L);

        handler = new SignatureHandler(signatureService, appConfig);
    }

    @Test
    void handlePost_validRequest_returnsSignature() throws IOException {
        setupRequest("POST", "application/json;charset=utf-8", "{\"msg\": \"Hello\"}");
        when(signatureService.sign("Hello")).thenReturn("fake-signature");

        handler.handle(exchange);

        assertResponse(200, "{\"signature\":\"fake-signature\"}");
        assertEquals("application/json;charset=utf-8", responseHeaders.getFirst("Content-Type"));
    }

    @Test
    void handlePost_invalidContentType_sendsUnsupportedMediaType() throws IOException {
        setupRequest("POST", "text/plain", "just text");

        handler.handle(exchange);

        assertResponse(415, "unsupported_media_type");
    }

    @Test
    void handlePost_payloadTooLarge_sendsPayloadTooLarge() throws IOException {
        setupRequest("POST", "application/json;charset=utf-8", null);
        when(requestHeaders.getFirst("Content-Length")).thenReturn("10000"); // Больше лимита 8192

        handler.handle(exchange);

        assertResponse(413, "payload_too_large");
    }

    @Test
    void handlePost_invalidJson_sendsInvalidJson() throws IOException {
        setupRequest("POST", "application/json;charset=utf-8", "{invalid}");

        handler.handle(exchange);

        assertResponse(400, "invalid_json");
    }

    @Test
    void handlePost_blankMessage_sendsInvalidMessage() throws IOException {
        setupRequest("POST", "application/json;charset=utf-8", "{\"msg\": \"\"}");

        handler.handle(exchange);

        assertResponse(400, "invalid_message");
    }
}