package io.github.martinez1337.hmac.exception;

public class SigningException extends RuntimeException {
    public SigningException() {
    }

    public SigningException(String msg) {
        super(msg);
    }

    public SigningException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SigningException(Throwable cause) {
        super(cause);
    }
}
