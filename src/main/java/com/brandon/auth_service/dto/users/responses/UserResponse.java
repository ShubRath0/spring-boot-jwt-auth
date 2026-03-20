package com.brandon.auth_service.dto.users.responses;

import java.util.UUID;

import com.brandon.auth_service.enums.Role;
import com.brandon.auth_service.models.User;

/**
 * Data Transfer Object representing a user's public profile information.
 * <p>
 * This record is used to send sanitized user data back to the client, ensuring
 * sensitive information like passwords are not exposed.
 * </p>
 * 
 * @param id        The unique identifier of the user.
 * @param email     The user's registered email address.
 * @param firstName the user's first name.
 * @param lastName  the user's last name.
 * @param role      The security {@link Role} assigned to the user.
 */
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role) {

    /**
     * Static factory method to map a {@link User} entity to a {@link UserResponse}
     * DTO.
     * 
     * @param user The persistent user entity to convert.
     * @return A new instance of UserResponse populated with the entity's data.
     */
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole());
    }
};