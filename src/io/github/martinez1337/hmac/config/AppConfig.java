package io.github.martinez1337.hmac.config;

import com.google.gson.Gson;
import io.github.martinez1337.hmac.codec.Codec;
import io.github.martinez1337.hmac.exception.ConfigLoadException;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.security.Key;

public class AppConfig {
    private final ConfigData configData;
    private final Key hmacKey;

    public AppConfig(ConfigData data, Key hmacKey) {
        this.configData = data;
        this.hmacKey = hmacKey;
    }

    public static AppConfig loadFromFile(String path, Gson gson, Codec codec) {
        try (FileReader reader = new FileReader(path)) {
            ConfigData data = gson.fromJson(reader, ConfigData.class);
            data.validate();

            byte[] decodedKey = codec.decode(data.getSecret());
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
}
