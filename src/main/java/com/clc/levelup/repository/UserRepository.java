package com.clc.levelup.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.clc.levelup.model.User;

/*
 * Team note:
 * Repository backing registration and login lookups.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /** Find a user by username (used for login). */
    Optional<User> findByUsername(String username);

    /** Fast existence check to block duplicate usernames. */
    boolean existsByUsername(String username);

    /** Fast existence check to block duplicate emails. */
    boolean existsByEmail(String email);
}
