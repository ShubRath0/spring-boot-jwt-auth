package com.brandon.auth_service.models;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.brandon.auth_service.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user account within the system and serves as the primary data
 * model for authentication.
 * 
 * This entity implements {@link UserDetails}to facilitate seamless integration
 * with Spring Security's authentication provider, bridging the gap between the
 * database persistent state and the security framework's requirements.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    /** The unique identifier for the user, stored as a UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    /** The user's unique login and contact email address. */
    private String email;

    /** The hashed password string; ignored in JSON response for security. */
    @JsonIgnore
    private String password;

    /** The user's legal first name. */
    private String firstName;

    /** The user's legal last name. */
    private String lastName;

    /**
     * The security level or group assigned to the user. Persisted as a String named
     * of the {@link Role} enum.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * The IPv4 or IPv6 address recorded at the time of account creation. Marked as
     * non-updatable for audit integrity.
     */
    @Column(name = "registration_ip", updatable = false)
    private String registrationIp;

    /**
     * Returns the unique identifier used for authentication.
     * 
     * @return The string representation of the User's {@link UUID}.
     */
    @Override
    public String getUsername() {
        return this.id.toString();
    }

    /**
     * Maps the user's role to a Spring Security authority.
     * <p>
     * The role is prefixed with the "ROLE_" to comply with Spring Security's
     * default role-based authorization naming conventions.
     * </p>
     * 
     * @return A collection containing the {@link SimpleGrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }
}