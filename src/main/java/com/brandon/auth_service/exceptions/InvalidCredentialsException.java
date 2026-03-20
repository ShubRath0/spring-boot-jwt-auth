package com.brandon.auth_service.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid Username or Password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
