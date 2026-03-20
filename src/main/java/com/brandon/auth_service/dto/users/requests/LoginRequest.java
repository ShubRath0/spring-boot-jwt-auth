package com.brandon.auth_service.dto.users.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a user's login credentials.
 * <p>
 * This record captures the necesary information to authenticate a user.
 * It utilizes Jakarta Validation annotations to ensure that the inbound payload
 * meets the security and formatting requirements before reaching the service
 * layer.
 * </p>
 * 
 * @param email    The user's registered email address; must be a well-formed
 *                 format.
 * @param password The user's plain-text password; must meet minimum length
 *                 requirements.
 */
public record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email") String email,
        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password) {
}
