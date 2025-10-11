package com.clc.levelup.service;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.clc.levelup.dto.UserRegistration; // simple DTO used by emulateCreate(..)
import com.clc.levelup.model.Role;
import com.clc.levelup.model.User;
import com.clc.levelup.repository.RoleRepository;
import com.clc.levelup.repository.UserRepository;

/*
 * Team note:
 * Handles registration and simple lookups.
 * We attach ROLE_USER after saving a new account.
 * Password hashing and full security come in M6.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate; // used for user_roles join insert

    /** Spring injects the repositories + JdbcTemplate. */
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Register a new user.
     * Steps:
     *  1) Block duplicate usernames/emails.
     *  2) Save the user (raw password for M4 only).
     *  3) Attach ROLE_USER in user_roles.
     * @throws IllegalArgumentException if username/email already used
     */
    public User register(String username, String rawPassword, String email) {
        // Check uniqueness up front to give a clean message to the user.
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Save the user; Spring Data sets the id on the returned object.
        User saved = userRepository.save(new User(username, rawPassword, email, true));

        // Look up the ROLE_USER row. We seeded it in the DDL.
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER missing in roles table"));

        // Insert join row so this user has ROLE_USER.
        jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)",
                saved.getId(), roleUser.getId()
        );

        return saved;
    }

    /**
     * Find a user by username (used by login page for now).
     * In M6, Spring Security will handle this for us.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ===== M4 compatibility helpers (keeps older controllers/config working) =====

    /** Simple delegate used by existing validations in controllers/config. */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /** Simple delegate used by existing validations in controllers/config. */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Lookup by username OR email (handy for login).
     * We try username via repository, then do a tiny JDBC email query.
     */
    public Optional<User> findByEmailOrUsername(String input) {
        Optional<User> byUser = userRepository.findByUsername(input);
        if (byUser.isPresent()) return byUser;

        // Fallback: select by email using JdbcTemplate (keeps repo simple for M4).
        return jdbcTemplate.query(
                "SELECT id, username, password, email, enabled FROM users WHERE email = ? LIMIT 1",
                rs -> rs.next()
                        ? Optional.of(mapRowToUser(rs.getLong("id"),
                                                   rs.getString("username"),
                                                   rs.getString("password"),
                                                   rs.getString("email"),
                                                   rs.getBoolean("enabled")))
                        : Optional.empty(),
                input
        );
    }

    /**
     * Kept for older controller code that calls emulateCreate(dto).
     * Just forwards to register(..).
     */
    public User emulateCreate(UserRegistration dto) {
        return register(dto.getUsername(), dto.getPassword(), dto.getEmail());
    }

    // Small helper to map a JDBC row to our User model.
    private User mapRowToUser(Long id, String username, String password, String email, boolean enabled) {
        User u = new User(username, password, email, enabled);
        u.setId(id);
        return u;
    }
}
