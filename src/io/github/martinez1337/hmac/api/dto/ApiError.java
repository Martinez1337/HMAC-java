package io.github.martinez1337.hmac.api.dto;

public record ApiError(
    int httpStatusCode,
    String errorMessage
) {
    public static final ApiError INVALID_JSON = new ApiError(400, "invalid_json");
    public static final ApiError OPERATION_NOT_SUPPORTED = new ApiError(404, "operation_not_supported");
    public static final ApiError METHOD_NOT_ALLOWED = new ApiError(405, "method_not_allowed");
    public static final ApiError PAYLOAD_TOO_LARGE = new ApiError(413, "payload_too_large");
    public static final ApiError UNSUPPORTED_MEDIA_TYPE = new ApiError(415, "unsupported_media_type");

    public static final ApiError INTERNAL_ERROR = new ApiError(500, "internal_error");
}
