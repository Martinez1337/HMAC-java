package io.github.martinez1337.hmac.api.dto;

import io.github.martinez1337.hmac.api.model.ErrorCode;

public record ApiError(
    int httpStatusCode,
    String errorMessage
) {
    public ApiError(ErrorCode errorCode) {
        this(errorCode.getHttpStatusCode(), errorCode.getDefaultMessage());
    }

    public ApiError(ErrorCode errorCode, String detailMessage) {
        this(errorCode.getHttpStatusCode(), detailMessage);
    }
}
