package io.github.martinez1337.hmac.api.validation.technical;

import com.sun.net.httpserver.HttpExchange;
import io.github.martinez1337.hmac.api.dto.ApiError;
import io.github.martinez1337.hmac.api.model.ErrorCode;
import io.github.martinez1337.hmac.api.validation.PredicateValidator;
import io.github.martinez1337.hmac.api.validation.Rule;
import io.github.martinez1337.hmac.config.AppConfig;

import java.util.List;

public class HttpRequestValidator extends PredicateValidator<HttpExchange> {
    public HttpRequestValidator(AppConfig config) {
        super(List.of(
            new Rule<>(
                exchange -> {
                    String ct = exchange.getRequestHeaders().getFirst("Content-Type");
                    return ct == null || !ct.equalsIgnoreCase("application/json");
                },
                new ApiError(ErrorCode.UNSUPPORTED_MEDIA_TYPE)
            ),
            new Rule<>(
                exchange -> {
                    String cl = exchange.getRequestHeaders().getFirst("Content-Length");
                    return cl != null && Long.parseLong(cl) > config.getMaxPayloadSize();
                },
                new ApiError(ErrorCode.PAYLOAD_TOO_LARGE)
            )
        ));
    }
}
