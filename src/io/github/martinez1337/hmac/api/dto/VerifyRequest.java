package io.github.martinez1337.hmac.api.dto;

public record VerifyRequest(String msg, String signature) {
    public long totalLength() {
        return signature.getBytes().length + msg.getBytes().length;
    }
}
