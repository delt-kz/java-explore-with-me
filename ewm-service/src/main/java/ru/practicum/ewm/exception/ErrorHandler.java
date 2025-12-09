package ru.practicum.ewm.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiError> handleBusinessLogic(BusinessLogicException e) {
        return buildResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        return buildResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiError> handleJpaNotFound(Exception e) {
        return buildResponse(new NotFoundException("Entity not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        ApiError error = new ApiError(
                errors,
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().format(dateTimeFormatter),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException e) {

        List<String> errors = e.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        ApiError error = new ApiError(
                errors,
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().format(dateTimeFormatter),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return buildResponse(
                new IllegalArgumentException("Invalid parameter: " + e.getName()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingRequestParam(MissingServletRequestParameterException e) {
        String msg = String.format(
                "Required request parameter '%s' is missing",
                e.getParameterName()
        );

        ApiError error = new ApiError(
                Arrays.stream(e.getStackTrace()).limit(5)
                        .map(StackTraceElement::toString)
                        .toList(),
                msg,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().format(dateTimeFormatter),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        return buildResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildResponse(Exception e, HttpStatus status) {
        ApiError error = new ApiError(
                Arrays.stream(e.getStackTrace()).limit(5)
                        .map(StackTraceElement::toString)
                        .toList(),
                e.getMessage(),
                status.value(),
                LocalDateTime.now().format(dateTimeFormatter),
                status.getReasonPhrase()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String msg = "Duplicate value or constraint violation";

        if (e.getRootCause() != null) {
            msg = e.getRootCause().getMessage();
        }

        ApiError error = new ApiError(
                Arrays.stream(e.getStackTrace()).limit(5)
                        .map(StackTraceElement::toString)
                        .toList(),
                msg,
                HttpStatus.CONFLICT.value(),  // 409 — логично для unique constraint
                LocalDateTime.now().format(dateTimeFormatter),
                HttpStatus.CONFLICT.getReasonPhrase()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
