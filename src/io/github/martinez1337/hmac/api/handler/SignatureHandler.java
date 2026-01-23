package io.github.martinez1337.hmac.api.handler;

import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.api.dto.SignResponse;
import io.github.martinez1337.hmac.api.validation.domain.SignRequestValidator;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.api.dto.SignRequest;
import io.github.martinez1337.hmac.service.SignatureService;

import java.io.IOException;

public class SignatureHandler extends BaseHttpHandler {
    private final SignatureService signatureService;

    public SignatureHandler(SignatureService signatureService, AppConfig appConfig) {
        super(appConfig);
        this.signatureService = signatureService;
        log.info("SignatureHandler initialized.");
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        log.info("Starting signing request processing...");
        byte[] rawBody = validateAndRead(exchange);
        SignRequest req = parseJson(rawBody, SignRequest.class);

        log.debug("Running domain validation for SignRequest...");
        new SignRequestValidator(appConfig).validate(req);

        log.info("Generating HMAC signature for the message...");
        String signature = signatureService.sign(req.msg());

        SignResponse resp = new SignResponse(signature);

        log.info("Signature successfully generated and prepared for response.");
        sendResponse(exchange, gson.toJson(resp), 200);
    }
}
