package io.github.martinez1337.hmac.api;

import com.sun.net.httpserver.HttpServer;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.api.handler.SignatureHandler;
import io.github.martinez1337.hmac.api.handler.VerificationHandler;
import io.github.martinez1337.hmac.service.HmacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerHMAC {
    private static final Logger log = LoggerFactory.getLogger(ServerHMAC.class);

    private final HttpServer httpServer;
    private final AppConfig appConfig;

    public ServerHMAC(AppConfig appConfig) throws IOException {
        this.appConfig = appConfig;
        this.httpServer = HttpServer.create(new InetSocketAddress(appConfig.getPort()), 0);
        HmacService hmacService = new HmacService(appConfig.getHmacKey(), appConfig.getAlgorithm());

        log.info("HmacService initialized with algorithm: {}", appConfig.getAlgorithm());

        httpServer.createContext("/sign", new SignatureHandler(hmacService, appConfig));
        httpServer.createContext("/verify", new VerificationHandler(hmacService, appConfig));
    }

    public void start() {
        log.info("Starting server on port {}", appConfig.getPort());
        httpServer.start();
    }

    public void stop() {
        log.info("Stopping server on port {}", appConfig.getPort());
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        AppConfig config = AppConfig.loadFromFile("config.json");

        ServerHMAC serverHMAC = new ServerHMAC(config);

        serverHMAC.start();
    }
}
