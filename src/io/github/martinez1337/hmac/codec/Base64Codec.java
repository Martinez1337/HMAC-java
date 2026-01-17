package io.github.martinez1337.hmac.codec;

import java.util.Base64;

public class Base64Codec implements Codec {

    @Override
    public String encode(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decode(String str) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("String is null or blank");
        }
        return Base64.getDecoder().decode(str);
    }
}
