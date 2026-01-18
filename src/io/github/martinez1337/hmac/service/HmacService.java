package io.github.martinez1337.hmac.service;

import io.github.martinez1337.hmac.exception.SignatureVerificationException;
import io.github.martinez1337.hmac.exception.SigningException;

import javax.crypto.Mac;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;

public class HmacService implements SignatureService {
    private final Key key;
    private final String algorithm;

    public HmacService(Key key, String algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }

    @Override
    public byte[] sign(byte[] data) {
        if (data == null) {
            throw new SigningException("Data to sign must not be null");
        }
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new SigningException("Failed to generate HMAC signature", e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {
        if (data == null) {
            throw new SignatureVerificationException("Data to verify must not be null");
        }
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] generatedSignature = mac.doFinal(data);
            return MessageDigest.isEqual(signature, generatedSignature);
        } catch (GeneralSecurityException e) {
            throw new SignatureVerificationException("Failed to verify HMAC signature", e);
        }
    }
}
