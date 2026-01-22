package io.github.martinez1337.hmac.api.validation;

import io.github.martinez1337.hmac.exception.ApiException;

public interface Validator<T> {
    void validate(T target) throws ApiException;
}
