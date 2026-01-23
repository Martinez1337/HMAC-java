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

        log.info("Initializing ServerHMAC on port {}...", appConfig.getPort());

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(appConfig.getPort()), 0);

            HmacService hmacService = new HmacService(appConfig.getHmacKey(), appConfig.getAlgorithm());
            log.info("HmacService successfully initialized. Algorithm: {}, Max Payload: {} bytes",
                    appConfig.getAlgorithm(), appConfig.getMaxPayloadSize());

            httpServer.createContext("/sign", new SignatureHandler(hmacService, appConfig));
            log.debug("Context registered: /sign");

            httpServer.createContext("/verify", new VerificationHandler(hmacService, appConfig));
            log.debug("Context registered: /verify");

        } catch (IOException e) {
            log.error("Failed to initialize HttpServer on port {}: {}", appConfig.getPort(), e.getMessage());
            throw e;
        }
    }

    public void start() {
        log.info("Server is starting...");
        httpServer.start();
        log.info("Server successfully started and listening on http://localhost:{}/", appConfig.getPort());
    }

    public void stop() {
        log.info("Shutting down server on port {}...", appConfig.getPort());
        httpServer.stop(0);
        log.info("Server stopped.");
    }

    public static void main(String[] args) {
        try {
            log.info("Loading configuration...");
            AppConfig config = AppConfig.loadFromFile("config.json");

            ServerHMAC serverHMAC = new ServerHMAC(config);
            serverHMAC.start();
        } catch (Exception e) {
            log.error("Fatal error during application startup", e);
            System.exit(1);
        }
    }
}
