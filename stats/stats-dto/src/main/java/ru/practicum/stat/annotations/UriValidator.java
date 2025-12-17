package ru.practicum.stat.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;

public class UriValidator implements ConstraintValidator<ValidUri, String> {
    @Override
    public boolean isValid(String uri, ConstraintValidatorContext context) {
        if (uri == null || uri.isBlank()) {
            return false;
        }
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
