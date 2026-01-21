package io.github.martinez1337.hmac.config;

import io.github.martinez1337.hmac.codec.Base64Codec;
import io.github.martinez1337.hmac.exception.ConfigLoadException;
import io.github.martinez1337.hmac.util.GsonFactory;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.security.Key;

public class AppConfig {
    public static final int JSON_OVER_HEAD_BYTES = 1024;

    private final ConfigData configData;
    private final Key hmacKey;

    public AppConfig(ConfigData data, Key hmacKey) {
        this.configData = data;
        this.hmacKey = hmacKey;
    }

    public static AppConfig loadFromFile(String path) {
        try (FileReader reader = new FileReader(path)) {
            ConfigData data = GsonFactory.createDefault().fromJson(reader, ConfigData.class);
            data.validate();

            byte[] decodedKey = Base64Codec.decode(data.getSecret(), Base64Codec.Mode.STANDARD);
            Key hmacKey = new SecretKeySpec(decodedKey, data.getHmacAlg());

            return new AppConfig(data, hmacKey);
        } catch (IllegalArgumentException e) {
            throw new ConfigLoadException("Invalid data format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ConfigLoadException("Fatal: Failed to load configuration", e);
        }
    }

    public int getPort() {
        return configData.getListenPort();
    }

    public Key getHmacKey() {
        return hmacKey;
    }

    public String getAlgorithm() {
        return configData.getHmacAlg();
    }

    public long getMaxMsgSizeBytes() {
        return configData.getMaxMsgSizeBytes();
    }

    public long getMaxPayloadSize() {
        return configData.getMaxMsgSizeBytes() + JSON_OVER_HEAD_BYTES;
    }
}
