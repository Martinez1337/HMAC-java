package io.github.martinez1337.hmac.config;

import com.google.gson.Gson;
import io.github.martinez1337.hmac.codec.Codec;
import io.github.martinez1337.hmac.exception.ConfigLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    private Gson gson;
    private Codec stubCodec;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        stubCodec = new Codec() {
            @Override
            public String encode(byte[] bytes) { return "encoded"; }
            @Override
            public byte[] decode(String str) {
                return "12345678901234567890123456789012".getBytes();
            }
        };
    }

    @Test
    void loadFromFile_validJson_returnsConfigObject() throws IOException {
        Path configPath = tempDir.resolve("config.json");
        String json = "{" +
                "\"listenPort\": 8080," +
                "\"secret\": \"some-secret\"," +
                "\"hmacAlg\": \"HmacSHA256\"," +
                "\"maxMsgSizeBytes\": 1024" +
                "}";
        Files.writeString(configPath, json);

        AppConfig config = AppConfig.loadFromFile(configPath.toString(), gson, stubCodec);

        assertAll("Config fields checking",
                () -> assertEquals(8080, config.getPort()),
                () -> assertEquals("HmacSHA256", config.getAlgorithm()),
                () -> assertEquals(1024, config.getMaxPayloadSize()),
                () -> assertNotNull(config.getHmacKey())
        );
    }

    @Test
    void loadFromFile_fileDoesNotExist_throwsConfigLoadException() {
        assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile("non_existent_file.json", gson, stubCodec);
        });
    }

    @Test
    void loadFromFile_invalidJson_throwsConfigLoadException() throws IOException {
        Path configPath = tempDir.resolve("invalid.json");
        Files.writeString(configPath, "{ \"invalid\": json ");

        assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile(configPath.toString(), gson, stubCodec);
        });
    }

    @Test
    void loadFromFile_codecThrowsException_throwsConfigLoadException() throws IOException {
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, "{\"secret\":\"bad!\", \"hmacAlg\":\"HmacSHA256\"}");

        Codec failingCodec = new Codec() {
            @Override
            public String encode(byte[] bytes) { return ""; }
            @Override
            public byte[] decode(String str) {
                throw new IllegalArgumentException("Invalid Base64");
            }
        };

        ConfigLoadException ex = assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile(configPath.toString(), gson, failingCodec);
        });

        assertTrue(ex.getMessage().contains("Invalid data format"));
    }
}