package io.github.martinez1337.hmac.api.validation.domain;

import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.SignRequest;
import io.github.martinez1337.hmac.api.model.ErrorCode;
import io.github.martinez1337.hmac.api.validation.PredicateValidator;
import io.github.martinez1337.hmac.api.validation.Rule;
import io.github.martinez1337.hmac.config.AppConfig;

import java.util.List;

public class SignRequestValidator extends PredicateValidator<SignRequest> {
    public SignRequestValidator(AppConfig config) {
        super(List.of(
            new Rule<>(
                req -> req.msg() == null || req.msg().isBlank(),
                new ApiError(ErrorCode.INVALID_MESSAGE)
            ),
            new Rule<>(
                req -> req.msg().getBytes().length > config.getMaxMsgSizeBytes(),
                new ApiError(ErrorCode.PAYLOAD_TOO_LARGE, "message_too_large")
            )
        ));
    }
}
