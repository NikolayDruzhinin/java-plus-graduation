package ru.practicum.ewm.exception;

public class EventStateConflictException extends RuntimeException {
    public EventStateConflictException(String message) {
        super(message);
    }
}
