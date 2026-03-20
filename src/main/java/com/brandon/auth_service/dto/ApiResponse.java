package com.brandon.auth_service.dto;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        boolean success,
        String error,
        String message,
        String path,
        T data,
        Map<String, String> fieldErrors) {
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data, String path) {
        return new ApiResponse<>(
                Instant.now(),
                status.value(),
                true,
                null,
                message,
                path,
                data,
                null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String path,
            Map<String, String> fieldErrors) {
        return new ApiResponse<>(
                Instant.now(),
                status.value(),
                false,
                status.getReasonPhrase(),
                message,
                path,
                null,
                fieldErrors);
    }
}
