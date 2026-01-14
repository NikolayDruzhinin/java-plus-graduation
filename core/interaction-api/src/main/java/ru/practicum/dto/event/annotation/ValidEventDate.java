package ru.practicum.dto.event.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEventTimeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventDate {
    String message() default "Date and time are not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
