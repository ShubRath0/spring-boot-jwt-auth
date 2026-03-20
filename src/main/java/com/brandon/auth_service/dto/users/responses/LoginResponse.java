package com.brandon.auth_service.dto.users.responses;

/**
 * Data Transfer Object returned upon a successful authentication attempt.
 * <p>
 * This record ancapsulates the security context required for subsequent
 * authorized requests and provides the client with the authenticated user's
 * profile information for UI personalizations.
 * </p>
 * 
 * @param authorization The {@link AuthDetails} containing the bearer token and
 *                      expiration.
 * @param user          The {@link UserResponse} profile data for the logged-in
 *                      user.
 */
public record LoginResponse(
        AuthDetails authorization,
        UserResponse user) {
}
