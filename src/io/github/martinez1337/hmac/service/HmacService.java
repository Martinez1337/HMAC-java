package io.github.martinez1337.hmac.service;

import io.github.martinez1337.hmac.codec.Base64Codec;
import io.github.martinez1337.hmac.exception.SignatureVerificationException;
import io.github.martinez1337.hmac.exception.SigningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;

public class HmacService implements SignatureService {
    public static final Logger log = LoggerFactory.getLogger(HmacService.class);

    private final Key key;
    private final String algorithm;

    public HmacService(Key key, String algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }

    @Override
    public String sign(String data) {
        if (data == null || data.isBlank()) {
            log.warn("Attempted to sign null data");
            throw new SigningException("Data to sign must not be null");
        }
        byte[] msgBytes = data.getBytes(StandardCharsets.UTF_8);
        log.debug("Signing data, size: {} bytes", msgBytes.length);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] result = mac.doFinal(msgBytes);
            log.debug("Signature generated successfully");
            return Base64Codec.encode(result, Base64Codec.Mode.URL);
        } catch (GeneralSecurityException e) {
            throw new SigningException("Failed to generate HMAC signature for algorithm: " + algorithm, e);
        }
    }

    @Override
    public boolean verify(String data, String signature) {
        if (data == null) {
            log.warn("Verification failed: data or signature is null");
            throw new SignatureVerificationException("Data to verify must not be null");
        }
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] msgBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = Base64Codec.decode(signature, Base64Codec.Mode.URL);
            byte[] generatedSignature = mac.doFinal(msgBytes);

            boolean isValid = MessageDigest.isEqual(signBytes, generatedSignature);

            if (isValid) {
                log.debug("Signature verification PASSED");
            } else {
                log.warn("Signature verification FAILED for data of size {} bytes", msgBytes.length);
            }
            return isValid;
        } catch (GeneralSecurityException e) {
            throw new SignatureVerificationException("Crypto provider error during verification", e);
        }
    }
}
