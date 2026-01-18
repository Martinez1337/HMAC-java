package io.github.martinez1337.hmac.exception;

public class SignatureVerificationException extends RuntimeException {
    public SignatureVerificationException() {
    }

    public SignatureVerificationException(String message) {
        super(message);
    }

    public SignatureVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureVerificationException(Throwable cause) {
        super(cause);
    }
}
