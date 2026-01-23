package io.github.martinez1337.hmac.http.handler;

import io.github.martinez1337.hmac.api.dto.SignRequest;
import io.github.martinez1337.hmac.api.dto.SignResponse;
import io.github.martinez1337.hmac.api.handler.SignatureHandler;
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

    @BeforeEach
    void setUp() {
        handler = new SignatureHandler(signatureService, appConfig);
    }

    @Test
    void handlePost_validRequest_returnsSignature() throws IOException {
        SignRequest req = new SignRequest("Hello");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));
        when(signatureService.sign(req.msg())).thenReturn("fake-signature");

        handler.handle(exchange);

        SignResponse resp = new SignResponse("fake-signature");
        assertResponse(200, gson.toJson(resp));
        assertEquals(BASE_CONTENT_TYPE, responseHeaders.getFirst("Content-Type"));
    }

    @Test
    void handlePost_blankMessage_sendsInvalidMessage() throws IOException {
        SignRequest req = new SignRequest("");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));

        handler.handle(exchange);

        assertResponse(400, "invalid_message");
    }
}