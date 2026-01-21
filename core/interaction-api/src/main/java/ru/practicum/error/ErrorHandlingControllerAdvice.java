package ru.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Ошибка валидации поля %s: %s. Некорректное значение: %s",
                        error.getField(),
                        error.getDefaultMessage(),
                        Objects.toString(error.getRejectedValue(), "null")))
                .collect(Collectors.toList());

        List<String> globalErrors = e.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        fieldErrors.addAll(globalErrors);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", fieldErrors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> String.format("Ошибка: %s. Значение: %s",
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.toList());

        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConflictPropertyConstraintException(ConflictPropertyConstraintException e) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConflictRelationsConstraintException(ConflictRelationsConstraintException e) {
        return new ErrorResponse(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFoundException(NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadEventTime(InvalidEventTimeException e) {
        String errorMessage = String.format("Validation error for event start time %s: the event cannot start earlier than %s.",
                e.getEventDate(), LocalDateTime.now().plusHours(2));
        return new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleConditionsNotMetException(ConditionsNotMetException e) {
        return new ErrorResponse(HttpStatus.FORBIDDEN, "The publication condition has been violated.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Request body could not be read properly", e.getMessage());
    }

}
