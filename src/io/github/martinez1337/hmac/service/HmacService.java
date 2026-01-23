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
        log.info("HmacService initialized with algorithm: {}", algorithm);
    }

    @Override
    public String sign(String data) {
        if (data == null || data.isBlank()) {
            log.warn("Signing failed: input data is null or blank");
            throw new SigningException("Data to sign must not be null or blank");
        }

        log.debug("Generating signature for data (size: {} bytes) using {}",
                data.length(), algorithm);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            log.debug("Signature generated successfully");
            return Base64Codec.encode(result, Base64Codec.Mode.URL);
        } catch (GeneralSecurityException e) {
            throw new SigningException("Failed to generate HMAC signature for algorithm: " + algorithm, e);
        }
    }

    @Override
    public boolean verify(String data, String signature) {
        if (data == null || signature == null) {
            log.warn("Verification rejected: missing data (is null: {}) or signature (is null: {})",
                    data == null, signature == null);
            throw new SignatureVerificationException("Data and signature must not be null");
        }

        log.debug("Verifying signature for data (size: {} bytes)...", data.length());

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] msgBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = Base64Codec.decode(signature, Base64Codec.Mode.URL);
            byte[] generatedSignature = mac.doFinal(msgBytes);

            boolean isValid = MessageDigest.isEqual(signBytes, generatedSignature);

            if (isValid) {
                log.debug("Signature verification successful");
            } else {
                log.warn("Signature verification failed: provided signature does not match");
            }
            return isValid;
        } catch (IllegalArgumentException e) {
            log.warn("Verification failed: invalid Base64Url encoding in signature");
            return false;
        } catch (GeneralSecurityException e) {
            throw new SignatureVerificationException("Crypto provider error during verification", e);
        }
    }
}
