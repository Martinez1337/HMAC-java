package io.github.martinez1337.hmac.codec;

import java.util.Base64;

public final class Base64Codec {

    private Base64Codec() {}

    public enum Mode {
        STANDARD,
        URL
    }

    public static String encode(byte[] bytes, Mode mode) {
        requireBytes(bytes);
        return encoder(mode).encodeToString(bytes);
    }

    public static byte[] decode(String str, Mode mode) {
        requireString(str);
        return decoder(mode).decode(str);
    }

    private static Base64.Encoder encoder(Mode mode) {
        return switch (mode) {
            case STANDARD -> Base64.getEncoder();
            case URL -> Base64.getUrlEncoder().withoutPadding();
        };
    }

    private static Base64.Decoder decoder(Mode mode) {
        return switch (mode) {
            case STANDARD -> Base64.getDecoder();
            case URL -> Base64.getUrlDecoder();
        };
    }

    private static void requireBytes(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("Bytes must not be null");
    }

    private static void requireString(String str) {
        if (str == null || str.isBlank())
            throw new IllegalArgumentException("String is null or blank");
    }
}

