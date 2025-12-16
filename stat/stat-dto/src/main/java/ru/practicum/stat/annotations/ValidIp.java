package ru.practicum.stat.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpAddressValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIp {
    String message() default "Incorrect IP-address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean supportsIpv6() default true;
}
