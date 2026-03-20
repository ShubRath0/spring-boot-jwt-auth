package com.brandon.auth_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.auth_service.models.User;

/**
 * Repository interface for {@link User} entities.
 * <p>
 * This interface handles all database abstraction logic, providing standard
 * CRUD operations and custom query methods for user identity management using
 * {@link UUID} as the primary key.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Checks if a user record exists associated with the given email.
     * 
     * @param email The email address to search for.
     * @return {@code true} if a record exists; {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a user by their unique email address.
     * 
     * @param email The email address to search for.
     * @return An {@link Optional} containing the found {@link User},
     *         or empty if no match is found.
     */
    Optional<User> findByEmail(String email);
}
