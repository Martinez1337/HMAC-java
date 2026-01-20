package io.github.martinez1337.hmac.service;

import io.github.martinez1337.hmac.exception.SignatureVerificationException;
import io.github.martinez1337.hmac.exception.SigningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
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
        log.info("HmacService initialized with algorithm: {}", algorithm);
    }

    @Override
    public byte[] sign(byte[] data) {
        if (data == null) {
            log.warn("Attempted to sign null data");
            throw new SigningException("Data to sign must not be null");
        }

        log.debug("Signing data, size: {} bytes", data.length);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] result = mac.doFinal(data);
            log.trace("Signature generated successfully");
            return result;
        } catch (GeneralSecurityException e) {
            throw new SigningException("Failed to generate HMAC signature for algorithm: " + algorithm, e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {
        if (data == null) {
            log.warn("Verification failed: data or signature is null");
            throw new SignatureVerificationException("Data to verify must not be null");
        }
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] generatedSignature = mac.doFinal(data);

            boolean isValid = MessageDigest.isEqual(signature, generatedSignature);

            if (isValid) {
                log.debug("Signature verification PASSED");
            } else {
                log.warn("Signature verification FAILED for data of size {} bytes", data.length);
            }
            return isValid;
        } catch (GeneralSecurityException e) {
            throw new SignatureVerificationException("Crypto provider error during verification", e);
        }
    }
}
