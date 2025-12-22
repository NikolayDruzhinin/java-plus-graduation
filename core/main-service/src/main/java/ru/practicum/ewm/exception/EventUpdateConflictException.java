package ru.practicum.ewm.exception;

public class EventUpdateConflictException extends RuntimeException {
    public EventUpdateConflictException(String message) {
        super(message);
    }
}
