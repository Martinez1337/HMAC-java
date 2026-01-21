package io.github.martinez1337.hmac.codec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class Base64CodecTest {

    @ParameterizedTest
    @EnumSource(Base64Codec.Mode.class)
    void encode_nullBytes_throwsIllegalArgumentException(Base64Codec.Mode mode) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Base64Codec.encode(null, mode)
        );
        assertEquals("Bytes must not be null", ex.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Base64Codec.Mode.class)
    void decode_nullString_throwsIllegalArgumentException(Base64Codec.Mode mode) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Base64Codec.decode(null, mode)
        );
        assertEquals("String is null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Base64Codec.Mode.class)
    void encodeDecode_roundTrip_matchesOriginalData(Base64Codec.Mode mode) {
        byte[] original = "Round-trip data 123!".getBytes(StandardCharsets.UTF_8);

        String encoded = Base64Codec.encode(original, mode);
        byte[] decoded = Base64Codec.decode(encoded, mode);

        assertArrayEquals(original, decoded);
    }

    @Test
    void standard_encode_returnsBase64String() {
        byte[] input = "Hello World".getBytes(StandardCharsets.UTF_8);
        String expected = "SGVsbG8gV29ybGQ=";

        String result = Base64Codec.encode(input, Base64Codec.Mode.STANDARD);

        assertEquals(expected, result);
    }

    @Test
    void standard_decode_invalidString_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Base64Codec.decode("SG!VsbG8h@#", Base64Codec.Mode.STANDARD)
        );
    }

    @Test
    void url_encode_returnsBase64UrlWithoutPadding() {
        byte[] input = new byte[]{60, 63, 62, 63};
        String expected = "PD8-Pw";

        String result = Base64Codec.encode(input, Base64Codec.Mode.URL);

        assertEquals(expected, result);
    }

    @Test
    void url_decode_invalidCharacters_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Base64Codec.decode("SGVsbG8+V29ybGQ/", Base64Codec.Mode.URL)
        );
    }
}
