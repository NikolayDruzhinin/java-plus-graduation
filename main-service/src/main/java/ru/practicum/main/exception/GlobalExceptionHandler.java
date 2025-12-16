package ru.practicum.main.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        String reason = "Not found";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        String reason = "Conflict";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleConflict(final ForbiddenException e) {
        String reason = "Forbidden";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        String reason = "Bad Request";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class,
            MethodArgumentNotValidException.class, MissingRequestValueException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestValidation(final Throwable e) {
        String reason = "Bad Request";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUncaught(final Exception e) {
        String reason = "Internal server error";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(Collections.singletonList(e.getMessage()), e.getMessage(), reason, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
