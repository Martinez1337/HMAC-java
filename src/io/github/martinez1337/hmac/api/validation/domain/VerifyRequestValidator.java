package io.github.martinez1337.hmac.api.validation.domain;

import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.VerifyRequest;
import io.github.martinez1337.hmac.api.model.ErrorCode;
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
                new ApiError(ErrorCode.INVALID_MESSAGE)
            ),
            new Rule<>(
                req -> isSigValid(req, config),
                new ApiError(ErrorCode.INVALID_SIGNATURE_FORMAT)
            ),
            new Rule<>(
                req -> req.totalLength()  > config.getMaxPayloadSize(),
                new ApiError(ErrorCode.PAYLOAD_TOO_LARGE, "body_too_large")
            ),
            new Rule<>(
                req -> req.msg().getBytes().length > config.getMaxMsgSizeBytes(),
                new ApiError(ErrorCode.PAYLOAD_TOO_LARGE, "message_too_large")
            )
        ));
    }

    private static boolean isSigValid(VerifyRequest req, AppConfig config) {
        if (req.signature() == null || req.signature().isBlank()) {
            return true;
        }
        if (req.signature().getBytes().length > config.getSigMaxSizeBytes()) {
            return true;
        }
        return !req.signature().matches(BASE64_URL_REGEX);
    }
}
