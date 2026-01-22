package io.github.martinez1337.hmac.api.validation;

import io.github.martinez1337.hmac.api.dto.ApiError;

import java.util.function.Predicate;

public record Rule<T>(
    Predicate<T> predicate,
    ApiError error
) {}
