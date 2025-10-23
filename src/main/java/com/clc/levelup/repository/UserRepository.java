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

    // Update (M6): email lookup used by password reset flow
    Optional<User> findByEmail(String email);
}
