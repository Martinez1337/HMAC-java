package io.github.martinez1337.hmac.api.validation.domain;

import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.VerifyRequest;
import io.github.martinez1337.hmac.api.validation.PredicateValidator;
import io.github.martinez1337.hmac.api.validation.Rule;
import io.github.martinez1337.hmac.config.AppConfig;

import java.util.List;

public class VerifyRequestValidator extends PredicateValidator<VerifyRequest> {
    private static final String BASE64_URL_REGEX = "^[a-zA-Z0-9\\-_]+$";

    public VerifyRequestValidator(AppConfig config) {
        super(List.of(
            new Rule<>(
                req -> req.msg() == null || req.msg().isBlank(),
                new ApiError(400, "invalid_message")
            ),
            new Rule<>(
                req -> {
                    if (req.signature() == null || req.signature().isBlank()) {
                        System.out.println("Here 1");
                        return true;
                    }
                    if (req.signature().getBytes().length > config.getSigMaxSizeBytes()) {
                        System.out.println("Here 2 -> " + req.signature().getBytes().length);
                        return true;
                    }
                    return !req.signature().matches(BASE64_URL_REGEX);
                },
                new ApiError(400, "invalid_signature_format")
            ),
            new Rule<>(
                req -> req.totalLength()  > config.getMaxPayloadSize(),
                new ApiError(413, "body_too_large")
            ),
            new Rule<>(
                req -> req.msg().getBytes().length > config.getMaxMsgSizeBytes(),
                new ApiError(413, "message_too_large")
            )
        ));
    }
}
