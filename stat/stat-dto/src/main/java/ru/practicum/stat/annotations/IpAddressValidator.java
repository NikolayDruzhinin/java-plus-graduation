package ru.practicum.stat.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddressValidator implements ConstraintValidator<ValidIp, String> {
    private boolean supportsIpv6;

    @Override
    public void initialize(ValidIp constraintAnnotation) {
        this.supportsIpv6 = constraintAnnotation.supportsIpv6();
    }

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext context) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            if (!supportsIpv6 && inetAddress instanceof java.net.Inet6Address) {
                return false; // Если IPv6 отключен, но адрес IPv6
            }
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
