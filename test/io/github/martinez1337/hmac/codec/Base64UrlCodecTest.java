package io.github.martinez1337.hmac.codec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Base64UrlCodecTest extends CodecTest {

    @Override
    protected void initCodec() {
        codec = new Base64UrlCodec();
    }

    @Test
    void encode_validBytes_returnsBase64UrlStringWithoutPadding() {
        // Данные подобраны так, чтобы в обычном Base64 появились '+' и '/' и потребовался padding '=='
        // Обычный Base64: "PD8+Pw==" -> URL Base64: "PD8-Pw"
        byte[] input = new byte[]{60, 63, 62, 63};
        String expected = "PD8-Pw";

        String result = codec.encode(input);

        assertEquals(expected, result, "The encoding result does not match the expected URL-safe Base64");
    }

    @Test
    void decode_validBase64UrlString_returnsByteArray() {
        String input = "PD8-Pw";
        byte[] expected = new byte[]{60, 63, 62, 63};

        byte[] result = codec.decode(input);

        assertArrayEquals(expected, result, "The decoded byte array does not match the original");
    }

    @Test
    void decode_invalidBase64UrlString_throwsIllegalArgumentException() {
        String invalidInput = "SGVsbG8+V29ybGQ/";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(invalidInput);
        }, "The method must throw an exception on non-URL-safe characters");
    }
}
