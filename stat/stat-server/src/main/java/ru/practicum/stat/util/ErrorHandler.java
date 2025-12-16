package ru.practicum.stat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stat.exception.BadRequestException;
import ru.practicum.stat.exception.InternalServerException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice("ru.practicum.stat.controller")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerError(final InternalServerException e, HttpStatus status) {
        log.info("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(status, "Error ...", e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse internalServerError(final BadRequestException e) {
        log.info("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Error ...", e.getMessage(), stackTrace);
    }
}
