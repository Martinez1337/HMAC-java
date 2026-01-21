package io.github.martinez1337.hmac.service;

public interface SignatureService {
    String sign(String msg);
    boolean verify(String msg, String signature);
}
