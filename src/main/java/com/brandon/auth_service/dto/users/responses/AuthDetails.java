package com.brandon.auth_service.dto.users.responses;

/**
 * Encapsulates the technical details of the authentication token.
 * <p>
 * This record provides the necessary information for the client to construct
 * subsequent authorized HTTP requests, typically by placing the {@code jwt}
 * into the 'Authorization' header using the specified {@code type}
 * </p>
 * 
 * @param type The authentication scheme being used (e.g., "Bearer").
 * @param jwt  The encoded JSON Web Token string containing the user's claims.
 */
public record AuthDetails(
        String type,
        String jwt) {

}
