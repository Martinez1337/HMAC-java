package io.github.martinez1337.hmac.config;

import com.google.gson.Gson;
import io.github.martinez1337.hmac.codec.Base64Codec;
import io.github.martinez1337.hmac.exception.ConfigLoadException;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.security.Key;

public class AppConfig {
    private static final String CONFIG_PATH = "config.json";
    private static ConfigData configData;
    private static Key hmacKey;

    static {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            configData = new Gson().fromJson(reader, ConfigData.class);

            if (configData == null) {
                throw new ConfigLoadException("Config file is empty");
            }
            configData.validate();

            byte[] decodedKey = new Base64Codec().decode(configData.getSecret());
            hmacKey = new SecretKeySpec(decodedKey, configData.getHmacAlg());
        } catch (IllegalArgumentException e) {
            throw new ConfigLoadException("Invalid data format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ConfigLoadException("Fatal: Failed to load configuration", e);
        }
    }

    public static int getPort() {
        return configData.getListenPort();
    }

    public static Key getHmacKey() {
        return hmacKey;
    }

    public static String getAlgorithm() {
        return configData.getHmacAlg();
    }

    public static long getMaxPayloadSize() {
        return configData.getMaxMsgSizeBytes();
    }
}
