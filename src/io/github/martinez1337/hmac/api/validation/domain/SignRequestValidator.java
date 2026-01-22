package io.github.martinez1337.hmac.api.validation.domain;

import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.dto.SignRequest;
import io.github.martinez1337.hmac.api.validation.PredicateValidator;
import io.github.martinez1337.hmac.api.validation.Rule;
import io.github.martinez1337.hmac.config.AppConfig;

import java.util.List;

public class SignRequestValidator extends PredicateValidator<SignRequest> {
    public SignRequestValidator(AppConfig config) {
        super(List.of(
            new Rule<>(
                req -> req.msg() == null || req.msg().isBlank(),
                new ApiError(400, "invalid_message")
            ),
            new Rule<>(
                req -> req.msg().getBytes().length > config.getMaxMsgSizeBytes(),
                new ApiError(413, "message_too_large")
            )
        ));
    }
}
