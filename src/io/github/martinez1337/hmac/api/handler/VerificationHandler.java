package io.github.martinez1337.hmac.api.handler;

import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.api.dto.VerifyRequest;
import io.github.martinez1337.hmac.api.dto.VerifyResponse;
import io.github.martinez1337.hmac.api.validation.domain.VerifyRequestValidator;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.service.SignatureService;

import java.io.IOException;

public class VerificationHandler extends BaseHttpHandler {
    private final SignatureService signatureService;

    public VerificationHandler(SignatureService signatureService, AppConfig appConfig) {
        super(appConfig);
        this.signatureService = signatureService;
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        log.info("Starting signature verification request processing...");

        byte[] rawBody = validateAndRead(exchange);
        VerifyRequest req = parseJson(rawBody, VerifyRequest.class);
        log.debug("JSON parsed successfully. Message length: {}, Signature length: {}",
                req.msg() != null ? req.msg().length() : 0,
                req.signature() != null ? req.signature().length() : 0);

        log.debug("Running domain validation for VerifyRequest...");
        new VerifyRequestValidator(appConfig).validate(req);

        log.debug("Verifying HMAC signature for the provided message...");
        boolean isSigValid = signatureService.verify(req.msg(), req.signature());

        if (isSigValid) {
            log.info("Verification result: SUCCESS");
        } else {
            log.warn("Verification result: FAILED (Invalid signature)");
        }

        VerifyResponse resp = new VerifyResponse(isSigValid);
        log.info("Verification process finished and prepared for response.");
        sendResponse(exchange, gson.toJson(resp), 200);
    }
}
