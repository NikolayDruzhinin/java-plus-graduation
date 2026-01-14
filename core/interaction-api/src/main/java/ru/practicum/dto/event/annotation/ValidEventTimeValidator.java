package ru.practicum.dto.event.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class ValidEventTimeValidator implements ConstraintValidator<ValidEventDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDateTime nowPlusTwoHours = LocalDateTime.now().plusHours(2);
        return value.isAfter(nowPlusTwoHours);
    }
}
