package io.github.martinez1337.hmac.exception;

import io.github.martinez1337.hmac.api.dto.ApiError;

public class ApiException extends RuntimeException {
    private final ApiError apiError;

    public ApiException(ApiError apiError) {
        super(apiError.errorMessage());
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }
}
