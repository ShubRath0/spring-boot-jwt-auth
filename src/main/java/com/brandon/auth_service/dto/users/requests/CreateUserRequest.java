package com.brandon.auth_service.dto.users.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for capturing new user registration details.
 * <p>
 * This record serves as the inbound contract for account creation.
 * It enforces strict validation rules to ensure data integrity and security
 * compliance before the user is persisted to the database.
 * </p>
 * 
 * @param email     The unique email address for the new account.
 * @param password  The desired password; enforced with a minimum length of 8.
 * @param firstName The user's legal first name.
 * @param lastName  The user's legal last name.
 */
public record CreateUserRequest(
        @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email") String email,
        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName) {
}
