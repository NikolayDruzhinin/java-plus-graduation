package ru.practicum.stat.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String stackTrace;

    public ErrorResponse(HttpStatus status, String error, String message, String stackTrace) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = error;
        this.message = message;
        this.stackTrace = stackTrace;
    }
}
