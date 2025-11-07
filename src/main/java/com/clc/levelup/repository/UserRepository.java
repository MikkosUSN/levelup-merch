package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.User;

/**
 * Repository interface for managing {@link User} account data.
 * Provides CRUD access and a set of helper methods for login, registration,
 * and password reset workflows.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Find a user by username (case-sensitive).
     * Used during authentication.
     * @param username unique username
     * @return optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     * @param username username to check
     * @return true if username is already taken
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     * @param email email address to check
     * @return true if email is already registered
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email address, ignoring case.
     * Useful for password reset flow and case-insensitive login.
     * @param email email address
     * @return optional containing user if found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Find a user by username, ignoring case.
     * Supports login when user input varies in case.
     * @param username username string
     * @return optional containing user if found
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Check if an email exists, ignoring case sensitivity.
     * @param email email address
     * @return true if email is already in use
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Check if a username exists, ignoring case sensitivity.
     * @param username username to check
     * @return true if username exists
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Find a user by email address (case-sensitive).
     * Retained for explicit use in password reset flow.
     * @param email email address
     * @return optional containing user if found
     */
    Optional<User> findByEmail(String email);
}
