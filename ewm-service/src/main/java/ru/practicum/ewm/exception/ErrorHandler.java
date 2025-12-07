package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ApiError handleException(Exception e) {
        return new ApiError(null,
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().format(dateTimeFormatter),
                null);
    }
}
