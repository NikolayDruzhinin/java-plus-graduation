package ru.practicum.application.api.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
