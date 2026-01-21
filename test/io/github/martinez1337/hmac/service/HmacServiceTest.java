package io.github.martinez1337.hmac.service;

import io.github.martinez1337.hmac.codec.Base64Codec;
import io.github.martinez1337.hmac.exception.SignatureVerificationException;
import io.github.martinez1337.hmac.exception.SigningException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class HmacServiceTest {

    private HmacService hmacService;
    private static final String ALGORITHM = "HmacSHA256";

    @BeforeEach
    void setUp() {
        // Тестовый ключ из RFC 4231 в формате Base64
        String base64Key = "CwsLCwsLCwsLCwsLCwsLCwsLCws=";
        byte[] decodedKey = Base64Codec.decode(base64Key, Base64Codec.Mode.STANDARD);

        Key testKey = new SecretKeySpec(decodedKey, ALGORITHM);
        hmacService = new HmacService(testKey, ALGORITHM);
    }

    @Test
    void sign_validData_returnsCorrectBase64Signature() {
        byte[] data = "Hi There".getBytes(StandardCharsets.UTF_8);
        String expectedBase64 = "sDRMYdjbOFNcqK/OrwvxK4gdwgDJgz2nJuk3bC4yz/c=";

        byte[] signature = hmacService.sign(data);
        String resultBase64 = Base64Codec.encode(signature, Base64Codec.Mode.STANDARD);

        assertEquals(expectedBase64, resultBase64, "The signature must comply with the RFC 4231 standard");
    }

    @Test
    void verify_validSignature_returnsTrue() {
        byte[] data = "Hi There".getBytes(StandardCharsets.UTF_8);
        String validBase64Sig = "sDRMYdjbOFNcqK/OrwvxK4gdwgDJgz2nJuk3bC4yz/c=";
        byte[] signature = Base64Codec.decode(validBase64Sig, Base64Codec.Mode.STANDARD);
        boolean result = hmacService.verify(data, signature);

        assertTrue(result, "Verification must be successful for a valid signature");
    }

    @Test
    void verify_tamperedData_returnsFalse() {
        byte[] originalData = "Hi There".getBytes(StandardCharsets.UTF_8);
        byte[] tamperedData = "Hi there".getBytes(StandardCharsets.UTF_8);
        byte[] signature = hmacService.sign(originalData);

        boolean result = hmacService.verify(tamperedData, signature);

        assertFalse(result, "Verification should fail if the data is changed");
    }

    @Test
    void sign_nullData_throwsSigningException() {
        assertThrows(SigningException.class, () -> hmacService.sign(null));
    }

    @Test
    void verify_nullData_throwsSignatureVerificationException() {
        byte[] someSignature = new byte[32];
        assertThrows(SignatureVerificationException.class, () -> hmacService.verify(null, someSignature));
    }
}