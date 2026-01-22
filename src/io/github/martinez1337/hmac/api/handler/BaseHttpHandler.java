package io.github.martinez1337.hmac.api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
    private static final Logger log = LoggerFactory.getLogger(BaseHttpHandler.class);

    protected final Gson gson = GsonFactory.createDefault();
    protected final AppConfig appConfig;

    protected BaseHttpHandler(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch(method) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "PUT" -> handlePut(exchange);
            case "PATCH" -> handlePatch(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> sendError(exchange, ApiError.METHOD_NOT_ALLOWED);
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        sendError(exchange, ApiError.OPERATION_NOT_SUPPORTED);
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        sendError(exchange, ApiError.OPERATION_NOT_SUPPORTED);
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        sendError(exchange, ApiError.OPERATION_NOT_SUPPORTED);
    }

    protected void handlePatch(HttpExchange exchange) throws IOException {
        sendError(exchange, ApiError.OPERATION_NOT_SUPPORTED);
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendError(exchange, ApiError.OPERATION_NOT_SUPPORTED);
    }

    protected void sendResponse(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendError(HttpExchange h, ApiError apiError) throws IOException {
        String jsonResponse = gson.toJson(Map.of("error", apiError.errorMessage()));
        sendResponse(h, jsonResponse, apiError.httpStatusCode());
    }

    protected byte[] validateAndRead(HttpExchange exchange) throws IOException {
        new HttpRequestValidator(appConfig).validate(exchange);
        log.debug("Reading body");
        byte[] body = exchange.getRequestBody().readAllBytes();
        if (body.length > appConfig.getMaxPayloadSize()) {
            throw new ApiException(ApiError.PAYLOAD_TOO_LARGE);
        }
        log.debug("Body is valid");
        return body;
    }

    protected <T> T parseJson(byte[] rawBody, Class<T> clazz) {
        try {
            String body = new String(rawBody, StandardCharsets.UTF_8);
            T dto = gson.fromJson(body, clazz);
            if (dto == null) throw new ApiException(ApiError.INVALID_JSON);
            return dto;
        } catch (JsonSyntaxException e) {
            throw new ApiException(ApiError.INVALID_JSON);
        }
    }
}
