package ru.practicum.stat.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UriValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUri {
    String message() default "Incorrect URI";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
