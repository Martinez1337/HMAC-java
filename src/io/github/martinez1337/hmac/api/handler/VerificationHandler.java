package io.github.martinez1337.hmac.api.handler;

import com.sun.net.httpserver.HttpExchange;
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

    }
}
