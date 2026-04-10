package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private int status;
    private String timestamp;
    private String reason;
}
