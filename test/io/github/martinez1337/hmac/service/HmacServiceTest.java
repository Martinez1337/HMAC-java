package io.github.martinez1337.hmac.service;

import io.github.martinez1337.hmac.codec.Base64Codec;
import io.github.martinez1337.hmac.exception.SignatureVerificationException;
import io.github.martinez1337.hmac.exception.SigningException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
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
    void sign_validData_returnsCorrectBase64UrlSignature() {
        String expectedBase64Url = "sDRMYdjbOFNcqK_OrwvxK4gdwgDJgz2nJuk3bC4yz_c";
        String result = hmacService.sign("Hi There");

        assertEquals(expectedBase64Url, result, "The signature must comply with the RFC 4231 standard");
    }

    @Test
    void verify_validSignature_returnsTrue() {
        String validBase64UrlSig = "sDRMYdjbOFNcqK_OrwvxK4gdwgDJgz2nJuk3bC4yz_c";
        boolean result = hmacService.verify("Hi There", validBase64UrlSig);

        assertTrue(result, "Verification must be successful for a valid signature");
    }

    @Test
    void verify_tamperedData_returnsFalse() {
        String signature = hmacService.sign("Hi There");
        boolean result = hmacService.verify("Hi there", signature);

        assertFalse(result, "Verification should fail if the data is changed");
    }

    @Test
    void sign_nullData_throwsSigningException() {
        assertThrows(SigningException.class, () -> hmacService.sign(null));
    }

    @Test
    void verify_nullData_throwsSignatureVerificationException() {
        assertThrows(SignatureVerificationException.class, () -> hmacService.verify(null, ""));
    }
}