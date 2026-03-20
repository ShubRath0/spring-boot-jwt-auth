package com.brandon.auth_service.dto.users.responses;

/**
 * Data Transfer Object sent to the client upon successful account creation.
 * <p>
 * This record acts as a composite reesponse, bundling both the authentication
 * credentials (JWT) and the sanitized user profile data.
 * </p>
 * 
 * @param authorization The {@link AuthDetails} containing the access token.
 * @param user          The {@link UserResponse} representing the newly created
 *                      user profile.
 */
public record RegistrationResponse(
        AuthDetails authorization,
        UserResponse user) {

}
