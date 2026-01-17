package io.github.martinez1337.hmac.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


public abstract class CodecTest {
    protected Codec codec;

    protected abstract void initCodec();

    @BeforeEach
    void setUp() {
        initCodec();
    }

    @Test
    void encode_nullBytes_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            codec.encode(null);
        });
        assertEquals("Bytes must not be null", exception.getMessage());
    }

    @Test
    void decode_nullString_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(null);
        });
        assertEquals("String is null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void decode_blankOrEmptyString_throwsIllegalArgumentException(String input) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(input);
        });
        assertEquals("String is null or blank", exception.getMessage());
    }

    @Test
    void encodeDecode_roundTrip_matchesOriginalData() {
        byte[] original = "Round-trip data 123!".getBytes(StandardCharsets.UTF_8);

        String encoded = codec.encode(original);
        byte[] decoded = codec.decode(encoded);

        assertArrayEquals(original, decoded);
    }
}
