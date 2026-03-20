package com.brandon.auth_service.services;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brandon.auth_service.dto.users.requests.CreateUserRequest;
import com.brandon.auth_service.dto.users.requests.LoginRequest;
import com.brandon.auth_service.dto.users.responses.AuthDetails;
import com.brandon.auth_service.dto.users.responses.LoginResponse;
import com.brandon.auth_service.dto.users.responses.RegistrationResponse;
import com.brandon.auth_service.dto.users.responses.UserResponse;
import com.brandon.auth_service.enums.Role;
import com.brandon.auth_service.exceptions.InvalidCredentialsException;
import com.brandon.auth_service.exceptions.RateLimitException;
import com.brandon.auth_service.exceptions.UserAlreadyExistsException;
import com.brandon.auth_service.exceptions.UserNotFoundException;
import com.brandon.auth_service.models.User;
import com.brandon.auth_service.repositories.UserRepository;
import com.brandon.auth_service.security.JwtService;
import com.brandon.auth_service.security.RateLimitService;

import io.github.bucket4j.Bucket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service class responsible for handling user-related operations,
 * specifically handling the business logic for user registration,
 * authentication, and identity persistence.
 * <p>
 * This service interacts with the {@link UserRepository} for data access
 * and utilizes {@link JwtService} for secure token generation upon
 * successful authentication or registration.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RateLimitService rateLimitService;

    /**
     * Registers a new user in the system after verifying the email is unique.
     * <p>
     * This method handles password hashing, persists the user entity with metadata
     * like the registration IP, and generates an initial JWT.
     * </p>
     * 
     * @param request        the registration details (email, password, names).
     * @param registrationIp The ip the request came from.
     * @return A {@link RegistrationResponse} containing the {@link UserResponse}
     *         and {@link AuthDetails}.
     * @throws UserAlreadyExistsException if the provided email is already in use.
     */
    @Transactional
    public RegistrationResponse registerNewUser(CreateUserRequest request, String registrationIp)
            throws UserAlreadyExistsException {
        Bucket bucket = rateLimitService.resolveBucket(registrationIp);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitException();
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .registrationIp(registrationIp)
                .role(Role.USER)
                .build();

        User createdUser = userRepository.save(user);
        String token = jwtService.generateToken(createdUser);

        UserResponse userResponse = UserResponse.fromEntity(createdUser);
        AuthDetails authDetails = new AuthDetails("Bearer", token);
        return new RegistrationResponse(authDetails, userResponse);
    }

    /**
     * Authenticates a user based on their credentials and generates a session
     * token.
     * 
     * @param data The login credentials (email and password).
     * @return A {@link LoginResponse} containing the {@link UserResponse} and
     *         {@link AuthDetails}.
     * @throws UserNotFoundException       if no account is associated with the
     *                                     provided email.
     * @throws InvalidCredentialsException if the password does not match the stored
     *                                     hash.
     */
    @Transactional
    public LoginResponse login(LoginRequest data)
            throws UserNotFoundException, InvalidCredentialsException {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserNotFoundException());

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(user);

        AuthDetails authDetails = new AuthDetails("Bearer", token);
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new LoginResponse(authDetails, userResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        return userRepository.findById(UUID.fromString(subject))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + subject));
    }
}