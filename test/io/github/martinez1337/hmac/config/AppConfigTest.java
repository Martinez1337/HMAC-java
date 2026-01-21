package io.github.martinez1337.hmac.config;

import io.github.martinez1337.hmac.exception.ConfigLoadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void loadFromFile_validJson_returnsConfigObject() throws IOException {
        Path configPath = tempDir.resolve("config.json");
        String json = "{" +
                "\"listenPort\": 8080," +
                "\"secret\": \"some+secret\"," +
                "\"hmacAlg\": \"HmacSHA256\"," +
                "\"maxMsgSizeBytes\": 1024" +
                "}";
        Files.writeString(configPath, json);

        AppConfig config = AppConfig.loadFromFile(configPath.toString());

        assertAll("Config fields checking",
                () -> assertEquals(8080, config.getPort()),
                () -> assertEquals("HmacSHA256", config.getAlgorithm()),
                () -> assertEquals(1024, config.getMaxMsgSizeBytes()),
                () -> assertNotNull(config.getHmacKey())
        );
    }

    @Test
    void loadFromFile_fileDoesNotExist_throwsConfigLoadException() {
        assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile("non_existent_file.json");
        });
    }

    @Test
    void loadFromFile_invalidJson_throwsConfigLoadException() throws IOException {
        Path configPath = tempDir.resolve("invalid.json");
        Files.writeString(configPath, "{ \"invalid\": json ");

        assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile(configPath.toString());
        });
    }

    @Test
    void loadFromFile_codecThrowsException_throwsConfigLoadException() throws IOException {
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, "{\"secret\":\"\", \"hmacAlg\":\"HmacSHA256\"}");

        ConfigLoadException ex = assertThrows(ConfigLoadException.class, () -> {
            AppConfig.loadFromFile(configPath.toString());
        });

        assertTrue(ex.getMessage().contains("Invalid data format"));
    }
}