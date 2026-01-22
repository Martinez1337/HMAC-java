package io.github.martinez1337.hmac.api.validation;

import io.github.martinez1337.hmac.exception.ApiException;

import java.util.List;

public class PredicateValidator<T> implements Validator<T> {
    private final List<Rule<T>> rules;

    public PredicateValidator(List<Rule<T>> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(T target) throws ApiException {
        for (Rule<T> rule : rules) {
            if (rule.predicate().test(target)) {
                throw new ApiException(rule.error());
            }
        }
    }
}
