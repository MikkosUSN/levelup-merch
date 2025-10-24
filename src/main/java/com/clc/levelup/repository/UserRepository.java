// src/main/java/com/clc/levelup/repository/UserRepository.java
package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.User;

/*
 * Repository for database access to User accounts.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /** Find user by username for login validation. */
    Optional<User> findByUsername(String username);

    /** Check if username already exists. */
    boolean existsByUsername(String username);

    /** Check if email already exists. */
    boolean existsByEmail(String email);

    // Update: case-insensitive find & existence checks (handy with mixed input)
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);

    // Update: email lookup used by password reset flow (explicit keep)
    Optional<User> findByEmail(String email);
}
