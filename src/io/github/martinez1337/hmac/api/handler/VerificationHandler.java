package io.github.martinez1337.hmac.api.handler;

import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.VerifyRequest;
import io.github.martinez1337.hmac.api.dto.VerifyResponse;
import io.github.martinez1337.hmac.api.validation.domain.VerifyRequestValidator;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.exception.ApiException;
import io.github.martinez1337.hmac.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VerificationHandler extends BaseHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(VerificationHandler.class);

    private final SignatureService signatureService;

    public VerificationHandler(SignatureService signatureService, AppConfig appConfig) {
        super(appConfig);
        this.signatureService = signatureService;
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            log.info("Validating request body to handle a request");
            byte[] rawBody = validateAndRead(exchange);
            VerifyRequest req = parseJson(rawBody, VerifyRequest.class);
            new VerifyRequestValidator(appConfig).validate(req);

            boolean isSigValid = signatureService.verify(req.msg(), req.signature());
            VerifyResponse resp = new VerifyResponse(isSigValid);
            sendResponse(exchange, gson.toJson(resp), 200);
        } catch (ApiException e) {
            log.error("Error occurred {} ", e.getApiError().errorMessage());
            sendError(exchange, e.getApiError());
        } catch (Exception e) {
            log.error("Internal error occurred ");
            sendError(exchange, ApiError.INTERNAL_ERROR);
        }
    }
}
