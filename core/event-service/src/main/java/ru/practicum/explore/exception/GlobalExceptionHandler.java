package ru.practicum.explore.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.exception.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String reasonMessage;
        String errorMessage;
        if (e.getMessage().contains("users_email_key")) {
            reasonMessage = "Creating user with already existing email";
            errorMessage = "Email already exists";
        } else if (e.getMessage().contains("categories_name_key")) {
            reasonMessage = "Creating category with already existing name";
            errorMessage = "Category already exists";
        } else {
            reasonMessage = "Integrity violation";
            errorMessage = e.getMessage();
        }
        log.error("Conflict: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(errorMessage))
                .message(errorMessage)
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            ServiceUnavailableException.class,
            BadRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(Exception e) {
        String reasonMessage;
        if (e instanceof ValidationException) {
            reasonMessage = "Validation failed";
        } else if (e instanceof MethodArgumentNotValidException) {
            reasonMessage = "Method argument not valid";
        } else if (e instanceof HandlerMethodValidationException) {
            reasonMessage = "Handler method not valid";
        } else if (e instanceof IllegalArgumentException) {
            reasonMessage = "Not valid request";
        } else if (e instanceof MissingServletRequestParameterException) {
            reasonMessage = "Missing request parameter";
        } else if (e instanceof ServiceUnavailableException) {
            reasonMessage = "Service unavailable";
        } else if (e instanceof BadRequestException) {
            reasonMessage = "Bad request";
        } else {
            reasonMessage = "Bad request";
        }
        log.error("BAD_REQUEST: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String reasonMessage = "Entity not found";
        log.error("NOT_FOUND: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Entity not found")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        String reasonMessage = "Conflict occurred";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("INTERNAL_SERVER_ERROR: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
