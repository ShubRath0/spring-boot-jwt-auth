package com.brandon.auth_service.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.brandon.auth_service.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for handling JSON Web Token (JWT) operations including
 * generation, parsing, and validation.
 * 
 * <p>
 * This service uses the HMAC-SHA algorithm to secure tokens, ensuring that user
 * identity claims remain immutable and verifiable during transit.
 * </p>
 */
@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String SECRET_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Generates a signed JWT for a specific user.
     * 
     * <p>
     * The token includes the {@link User} ID as the 'subject' and defines standard
     * claism for the 'issued at' and 'expiration'.
     * </p>
     * 
     * @param user The user entity providing the identity claim.
     * @return A compact, URL-safe JWT string.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the integrity and authenticity of a token.
     * 
     * <p>
     * This method verifies the cryptographic signature, checks the expiration
     * status, and ensures the subject matches the username of the provided
     * {@link UserDetails}.
     * </p>
     * 
     * @param jwt  The token string to validate.
     * @param user The user details to compare against the token payload.
     * @return {@code true} if the token is authentic, unexpired, and belongs to the
     *         user.
     */
    public boolean validateToken(String jwt, UserDetails user) {
        try {
            Claims claims = extractAllClaims(jwt);
            String id = claims.getSubject();
            return (id.equals(user.getUsername()) && !isTokenExpired(jwt));
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT Validation Failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token's expiration timestamp is in the past.
     * 
     * @param jwt the token string.
     * @return {@code true} if the current system time is after the token's 'exp'
     *         claim.
     */
    public boolean isTokenExpired(String jwt) {
        return extractAllClaims(jwt).getExpiration().before(new Date());
    }

    /**
     * Extracts the User ID (Subject) from the token's payload.
     * 
     * @param jwt The token string.
     * @return The unique user idenifier stored in the 'sub' claim.
     */
    public String extractUserId(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.getSubject();
    }

    /**
     * Decodes and verifies the JWT signature to extract the payload.
     * 
     * @param jwt The token string.
     * @return The {@link Claims} object containing the payload data.
     * @throws JwtException If the signature is invalid, expired, or malformed.
     */
    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    /**
     * Transforms the configured secret string into a cryptographic HMAC-SHA
     * SecretKey
     * 
     * @return A {@link SecretKey} derived from the application properties.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}