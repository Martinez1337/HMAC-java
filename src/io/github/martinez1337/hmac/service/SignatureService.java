package io.github.martinez1337.hmac.service;

public interface SignatureService {
    byte[] sign(byte[] data);
    boolean verify(byte[] data, byte[] signature);
}
