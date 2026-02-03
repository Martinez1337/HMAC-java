package io.github.martinez1337.hmac.api.model;

public enum ErrorCode {
    INVALID_JSON(400, "invalid_json"),
    INVALID_MESSAGE(400, "invalid_message"),
    INVALID_SIGNATURE_FORMAT(400, "invalid_signature_format"),
    OPERATION_NOT_SUPPORTED(404, "operation_not_supported"),
    METHOD_NOT_ALLOWED(405, "method_not_allowed"),
    PAYLOAD_TOO_LARGE(413, "payload_too_large"),
    UNSUPPORTED_MEDIA_TYPE(415, "unsupported_media_type"),
    INTERNAL_ERROR(500, "internal_error");

    private final int httpStatusCode;
    private final String defaultMessage;

    ErrorCode(int httpStatusCode, String defaultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.defaultMessage = defaultMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
