package com.brandon.auth_service.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("The email " + email + " is already registered.");
    }

    public UserAlreadyExistsException() {
        super("This email address is already registered.");
    }
}
