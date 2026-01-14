package io.github.martinez1337.hmac.config;

public class ConfigData {
    private String hmacAlg;
    private String secret;
    private int listenPort;
    private long maxMsgSizeBytes;

    private ConfigData() {}

    public void validate() {
        if (secret == null || secret.isBlank())
            throw new IllegalArgumentException("Secret key is missing in config");
        if (hmacAlg == null || hmacAlg.isBlank())
            throw new IllegalArgumentException("Algorithm is not specified");
        if (listenPort < 1 || listenPort > 65535)
            throw new IllegalArgumentException("Invalid port: " + listenPort);
        if (maxMsgSizeBytes <= 0)
            throw new IllegalArgumentException("Max message size must be positive");
    }

    public String getHmacAlg() {
        return hmacAlg;
    }

    public String getSecret() {
        return secret;
    }

    public int getListenPort() {
        return listenPort;
    }

    public long getMaxMsgSizeBytes() {
        return maxMsgSizeBytes;
    }
}
