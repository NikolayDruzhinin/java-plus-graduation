package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParticipantLimitReachedException extends RuntimeException {
    public ParticipantLimitReachedException(String message) {
        super(message);
    }
}