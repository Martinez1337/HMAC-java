package io.github.martinez1337.hmac.api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.martinez1337.hmac.api.model.ErrorCode;
import io.github.martinez1337.hmac.api.validation.technical.HttpRequestValidator;
import io.github.martinez1337.hmac.config.AppConfig;
import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.exception.ApiException;
import io.github.martinez1337.hmac.util.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Gson gson = GsonFactory.createDefault();
    protected final AppConfig appConfig;

    protected BaseHttpHandler(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        log.info("Processing {} request for path: {}", method, path);
        try {
            switch(method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "PUT" -> handlePut(exchange);
                case "PATCH" -> handlePatch(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> {
                    log.warn("Method {} not allowed for {}", method, path);
                    sendError(exchange, new ApiError(ErrorCode.METHOD_NOT_ALLOWED));
                }
            }
        } catch (ApiException e) {
            log.warn("API Error while handling request: {} (HTTP {})",
                    e.getApiError().errorMessage(), e.getApiError().httpStatusCode());
            sendError(exchange, e.getApiError());
        } catch (Exception e) {
            log.error("Unexpected server error during request processing", e);
            sendResponse(exchange, "{\"error\":\"Internal server error\"}", 500);
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        sendError(exchange, new ApiError(ErrorCode.OPERATION_NOT_SUPPORTED));
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        sendError(exchange, new ApiError(ErrorCode.OPERATION_NOT_SUPPORTED));
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        sendError(exchange, new ApiError(ErrorCode.OPERATION_NOT_SUPPORTED));
    }

    protected void handlePatch(HttpExchange exchange) throws IOException {
        sendError(exchange, new ApiError(ErrorCode.OPERATION_NOT_SUPPORTED));
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendError(exchange, new ApiError(ErrorCode.OPERATION_NOT_SUPPORTED));
    }

    protected void sendResponse(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(rCode, resp.length);

        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }

        log.info("Response sent. Status: {}, Payload size: {} bytes", rCode, resp.length);
    }

    protected void sendError(HttpExchange h, ApiError apiError) throws IOException {
        String jsonResponse = gson.toJson(Map.of("error", apiError.errorMessage()));
        sendResponse(h, jsonResponse, apiError.httpStatusCode());
    }

    protected byte[] validateAndRead(HttpExchange exchange) throws IOException {
        log.debug("Validating technical headers and reading request body...");
        new HttpRequestValidator(appConfig).validate(exchange);

        byte[] body = exchange.getRequestBody().readAllBytes();

        if (body.length > appConfig.getMaxPayloadSize()) {
            log.warn("Request rejected: Body size {} bytes exceeds limit of {} bytes",
                    body.length, appConfig.getMaxPayloadSize());
            throw new ApiException(new ApiError(ErrorCode.PAYLOAD_TOO_LARGE));
        }

        log.debug("Body successfully read ({} bytes)", body.length);
        return body;
    }

    protected <T> T parseJson(byte[] rawBody, Class<T> clazz) {
        try {
            String body = new String(rawBody, StandardCharsets.UTF_8);
            T dto = gson.fromJson(body, clazz);
            if (dto == null) {
                throw new ApiException(new ApiError(ErrorCode.INVALID_JSON));
            }
            return dto;
        } catch (JsonSyntaxException e) {
            throw new ApiException(new ApiError(ErrorCode.INVALID_JSON));
        }
    }
}
