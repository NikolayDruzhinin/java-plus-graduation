package ru.practicum.application.api.exception;

public class WrongDataException extends Exception {
    public WrongDataException(String message) {
        super(message);
    }
}