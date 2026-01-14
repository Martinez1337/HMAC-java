package io.github.martinez1337.hmac.exception;

public class ConfigLoadException extends RuntimeException {
    public ConfigLoadException() {}

    public ConfigLoadException(String msg) {
        super(msg);
    }

    public ConfigLoadException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConfigLoadException(Throwable cause) {
        super(cause);
    }
}
