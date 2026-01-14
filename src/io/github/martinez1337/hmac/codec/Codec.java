package io.github.martinez1337.hmac.codec;

public interface Codec {
    String encode(byte[] bytes);
    byte[] decode(String str);
}
