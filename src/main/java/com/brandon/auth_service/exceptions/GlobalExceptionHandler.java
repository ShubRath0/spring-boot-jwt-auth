package com.brandon.auth_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.brandon.auth_service.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(RuntimeException.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRunTime(RuntimeException ex, HttpServletRequest servletRequest) {
        logger.error("Internal Server Error: {}", ex.getMessage(), ex);
        return createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                servletRequest,
                "An unexpected error occured. Please try again later.",
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest servletRequest) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return createResponse(
                HttpStatus.BAD_REQUEST,
                servletRequest,
                ex.getMessage(),
                errors);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(UserAlreadyExistsException ex,
            HttpServletRequest servletRequest) {
        return createResponse(
                HttpStatus.CONFLICT,
                servletRequest,
                ex.getMessage(),
                null);
    }

    @ExceptionHandler({ InvalidCredentialsException.class, UserNotFoundException.class })
    private ResponseEntity<ApiResponse<Void>> handleAuthError(Exception ex, HttpServletRequest servletRequest) {
        return createResponse(
                HttpStatus.UNAUTHORIZED,
                servletRequest,
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(RateLimitException.class)
    private ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitException ex,
            HttpServletRequest servletRequest) {
        return createResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                servletRequest,
                ex.getMessage(),
                null);
    }

    private ResponseEntity<ApiResponse<Void>> createResponse(HttpStatus status, HttpServletRequest servletRequest,
            String message, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status, message, servletRequest.getRequestURI(), fieldErrors));
    }

}
