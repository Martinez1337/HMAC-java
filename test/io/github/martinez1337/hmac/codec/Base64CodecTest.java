package io.github.martinez1337.hmac.codec;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class Base64CodecTest extends CodecTest{

    @Override
    protected void initCodec() {
        codec = new Base64Codec();
    }

    @Test
    void encode_validBytes_returnsBase64String() {
        byte[] input = "Hello World".getBytes(StandardCharsets.UTF_8);
        String expected = "SGVsbG8gV29ybGQ=";

        String result = codec.encode(input);

        assertEquals(expected, result, "The encoding result does not match the expected one");
    }

    @Test
    void decode_validBase64String_returnsByteArray() {
        String input = "SGVsbG8gV29ybGQ=";
        byte[] expected = "Hello World".getBytes(StandardCharsets.UTF_8);

        byte[] result = codec.decode(input);

        assertArrayEquals(expected, result, "The decoded byte array does not match the original");
    }

    @Test
    void decode_invalidBase64String_throwsIllegalArgumentException() {
        // Символы '!' и '@' не входят в алфавит Base64
        String invalidInput = "SG!VsbG8h@#";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(invalidInput);
        }, "The method must throw an exception on invalid Base64 characters");
    }
}
