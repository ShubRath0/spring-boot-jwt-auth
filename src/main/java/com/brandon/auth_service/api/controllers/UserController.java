package com.brandon.auth_service.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandon.auth_service.dto.ApiResponse;
import com.brandon.auth_service.dto.users.requests.CreateUserRequest;
import com.brandon.auth_service.dto.users.requests.LoginRequest;
import com.brandon.auth_service.dto.users.responses.LoginResponse;
import com.brandon.auth_service.dto.users.responses.RegistrationResponse;
import com.brandon.auth_service.exceptions.RateLimitException;
import com.brandon.auth_service.models.User;
import com.brandon.auth_service.security.RateLimitService;
import com.brandon.auth_service.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST Controller for managing user identity and access.
 * <p>
 * This class provides the primary API endpoints for account registration and
 * authentication. It integrates with {@link RateLimitService} to prevent
 * brute-force attacks and {@link UserService} to handle core business logic.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Processes new user registrations with built-in rate limiting and IP tracking.
     * <p>
     * This endpoint captures the client's local address for audit purposes and
     * applies a token-bucket algorithm to prevent registration spam.
     * </p>
     * 
     * @param request        The validated {@link CreateUserRequest} details.
     * @param servletRequest The raw HTTP request used to extract the client IP.
     * @return A {@link ResponseEntity} containing the registration status and JWT.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RegistrationResponse>> register(@Valid @RequestBody CreateUserRequest request,
            HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();

        RegistrationResponse response = userService.registerNewUser(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "User created successfully.",
                        response,
                        servletRequest.getRequestURI()));
    }

    /**
     * Authenticates existing users and generates access tokens.
     * 
     * @param request The validated {@link LoginRequest} credentials.
     * @return A {@link ResponseEntity} containing the {@link LoginResponse} and
     *         JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "Successfully logged in.", response,
                        servletRequest.getRequestURI()));
    }

    /**
     * Verifies that the provided JWT is valid by accessing a protected endpoint.
     * <p>
     * If authentication succeeds, the authenticated user's information is injected
     * into the method via {@link AuthenticationPrincipal}
     * </p>
     * 
     * @param user The authenticated user resolved from the JWT's subject by Spring
     *             Security.
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with a
     *         success message confirming the token is vaild.
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Void>> testToken(@AuthenticationPrincipal User user,
            HttpServletRequest servletRequest) {
        String welcomeMessage = "Token is valid! Welcome back, " + user.getFirstName();
        return ResponseEntity
                .ok(ApiResponse.success(HttpStatus.OK, welcomeMessage, null,
                        servletRequest.getRequestURI()));
    }
}
