package ru.practicum.ewm.exception;

public class EventDateConflictException extends RuntimeException {
    public EventDateConflictException(String message) {
        super(message);
    }
}
