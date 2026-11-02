package com.khalel.onlinestore.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> notFound(ProductNotFoundException exception) {
        return error(404, exception.getMessage(), Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                field -> errors.putIfAbsent(field.getField(), field.getDefaultMessage()));
        return error(400, "Validation failed", errors);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = switch (status.value()) {
            case 400 -> "Invalid request";
            case 404 -> "Resource not found";
            case 405 -> "Method not allowed";
            case 415 -> "Unsupported media type";
            default -> "Request failed";
        };
        return new ResponseEntity<>(body(status.value(), message, Map.of()), headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unexpected(Exception exception) {
        return error(500, "An unexpected error occurred", Map.of());
    }

    private ResponseEntity<Object> error(int status, String message, Map<String, String> errors) {
        return ResponseEntity.status(status).body(body(status, message, errors));
    }

    private Map<String, Object> body(int status, String message, Map<String, String> errors) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status);
        result.put("message", message);
        result.put("timestamp", LocalDateTime.now());
        result.put("errors", errors);
        return result;
    }
}
