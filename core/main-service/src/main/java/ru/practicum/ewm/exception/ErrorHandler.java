package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMissingParams(MissingServletRequestParameterException e) {
        final List<Violation> violations = List.of(
                new Violation(e.getParameterName(), "Required parameter is missing"));
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        final List<Violation> violations = List.of(new Violation("NOT_FOUND", e.getMessage()));
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        final List<Violation> violations = List.of(new Violation("BAD_REQUEST", e.getMessage()));
        return new ErrorResponse(violations);
    }

    @ExceptionHandler({
            ValidationException.class,
            ParticipantLimitReachedException.class,
            EventUpdateConflictException.class,
            RequestProcessingException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictExceptions(RuntimeException e) {
        String errorMessage = e instanceof IllegalStateException
                ? "Operation not allowed for current state"
                : e.getMessage();

        log.warn("Conflict detected: {}", errorMessage);
        return new ErrorResponse(List.of(new Violation("CONFLICT", errorMessage)));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleThrowable(Throwable e) {
        log.error("Unhandled exception: ", e);
        return new ErrorResponse(List.of(
                new Violation("INTERNAL_ERROR", "Internal Server Error. Please try later.")
        ));
    }
}
