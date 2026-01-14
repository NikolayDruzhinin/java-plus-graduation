package ru.practicum.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {

    private final int status;
    private final String reason;
    private final List<String> messages;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String reason, List<String> messages) {
        this.status = status.value();
        this.reason = reason;
        this.messages = messages;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status, String reason, String message) {
        this.status = status.value();
        this.reason = reason;
        this.messages = List.of(message);
        this.timestamp = LocalDateTime.now();
    }
}