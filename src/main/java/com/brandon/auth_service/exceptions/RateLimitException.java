package com.brandon.auth_service.exceptions;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException() {
        super("Too many requests.");
    }
}
