package ru.practicum.exception;

public class ConflictRelationsConstraintException extends RuntimeException {
    public ConflictRelationsConstraintException(String message) {
        super(message);
    }
}
