package io.github.martinez1337.hmac.api.handler;

import io.github.martinez1337.hmac.api.dto.VerifyRequest;
import io.github.martinez1337.hmac.api.dto.VerifyResponse;
import io.github.martinez1337.hmac.service.SignatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificationHandlerTest extends BaseHttpHandlerTest {
    @Mock private SignatureService signatureService;

    @BeforeEach
    void setUp() {
        handler = new VerificationHandler(signatureService, appConfig);
    }

    @Test
    void handlePost_validRequest_returnsVerificationResult() throws IOException {
        VerifyRequest req = new VerifyRequest("Hello!", "UNurNy-h-u70N8iXZieKH8tDRMIuBNf12wGjth8pJBI");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));
        when(signatureService.verify(req.msg(), req.signature())).thenReturn(true);

        handler.handle(exchange);

        VerifyResponse resp = new VerifyResponse(true);
        assertResponse(200, gson.toJson(resp));
        assertEquals(BASE_CONTENT_TYPE, responseHeaders.getFirst("Content-Type"));
    }

    @Test
    void handlePost_invalidSig_returnsFalse() throws IOException {
        VerifyRequest req = new VerifyRequest("Hello!", "UNurNy-h-u70N8iXZieKH8tDRMIuBNf12wGjth8pJB1");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));
        when(signatureService.verify(req.msg(), req.signature())).thenReturn(false);

        handler.handle(exchange);

        VerifyResponse resp = new VerifyResponse(false);
        assertResponse(200, gson.toJson(resp));
        assertEquals(BASE_CONTENT_TYPE, responseHeaders.getFirst("Content-Type"));
    }

    @Test
    void handlePost_blankMessage_sendsInvalidMessage() throws IOException {
        VerifyRequest req = new VerifyRequest("", "some-sig");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));

        handler.handle(exchange);

        assertResponse(400, "invalid_message");
    }

    @Test
    void handlePost_blankSig_sendsInvalidMessage() throws IOException {
        VerifyRequest req = new VerifyRequest("some-msg", "");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));

        handler.handle(exchange);

        assertResponse(400, "invalid_signature_format");
    }

    @Test
    void handlePost_invalidBase64Url_sendsInvalidMessage() throws IOException {
        VerifyRequest req = new VerifyRequest("some-msg", "UNurNy/hu70N8iXZieKH8tDRMIuBNf12wGjth8pJBI");
        setupRequest("POST", BASE_CONTENT_TYPE, gson.toJson(req));

        handler.handle(exchange);

        assertResponse(400, "invalid_signature_format");
    }
}
