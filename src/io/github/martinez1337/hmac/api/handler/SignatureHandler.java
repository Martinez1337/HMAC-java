package io.github.martinez1337.hmac.api.handler;

import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.SignResponse;
import io.github.martinez1337.hmac.api.validation.domain.SignRequestValidator;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.api.dto.SignRequest;
import io.github.martinez1337.hmac.exception.ApiException;
import io.github.martinez1337.hmac.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SignatureHandler extends BaseHttpHandler {
    public static final Logger log = LoggerFactory.getLogger(SignRequest.class);

    private final SignatureService signatureService;

    public SignatureHandler(SignatureService signatureService, AppConfig appConfig) {
        super(appConfig);
        this.signatureService = signatureService;
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            log.info("Validating request body to handle a request");
            byte[] rawBody = validateAndRead(exchange);
            SignRequest req = parseJson(rawBody, SignRequest.class);
            new SignRequestValidator(appConfig).validate(req);

            String signature = signatureService.sign(req.msg());
            SignResponse resp = new SignResponse(signature);
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
