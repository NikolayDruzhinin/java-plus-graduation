package ru.practicum.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InvalidEventTimeException extends RuntimeException {
    private final LocalDateTime eventDate;

    public InvalidEventTimeException(LocalDateTime eventDate) {
        super("The event date is invalid.: " + eventDate);
        this.eventDate = eventDate;
    }

}
