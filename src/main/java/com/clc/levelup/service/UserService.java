// src/main/java/com/clc/levelup/service/UserService.java
package com.clc.levelup.service;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clc.levelup.dto.UserRegistration; // simple DTO used by emulateCreate(..)
import com.clc.levelup.model.Role;
import com.clc.levelup.model.User;
import com.clc.levelup.repository.RoleRepository;
import com.clc.levelup.repository.UserRepository;

/*
 * Handles registration and simple lookups.
 * We attach ROLE_USER after saving a new account.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate; // used for user_roles join insert
    private final PasswordEncoder passwordEncoder; // Update

    /** Spring injects the repositories + JdbcTemplate. */
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JdbcTemplate jdbcTemplate,
                       PasswordEncoder passwordEncoder) { // Update
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder; // Update
    }

    /**
     * Register a new user.
     * Steps:
     *  1) Block duplicate usernames/emails.
     *  2) Save the user (hash password).
     *  3) Attach ROLE_USER in user_roles.
     * @throws IllegalArgumentException if username/email already used
     */
    public User register(String username, String rawPassword, String email) {
        // Update: normalize inputs (trim; lowercase email)
        final String normUsername = safe(username);
        final String normEmail = safe(email).toLowerCase();

        // Accept either DB or explicit case-insensitive checks
        if (existsByUsernameIgnoreCase(normUsername)) { // Update
            throw new IllegalArgumentException("Username already exists.");
        }
        if (existsByEmailIgnoreCase(normEmail)) { // Update
            throw new IllegalArgumentException("Email already in use.");
        }

        // Hash password before save
        String hashed = passwordEncoder.encode(rawPassword);

        // Save user; Spring Data sets the id on returned object.
        User saved = userRepository.save(new User(normUsername, hashed, normEmail, true));

        // Look up ROLE_USER (seeded in DDL).
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER missing in roles table"));

        // Insert join row so this user has ROLE_USER.
        jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)",
                saved.getId(), roleUser.getId()
        );

        return saved;
    }

    /** Find a user by username (legacy helper). */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(safe(username));
    }

    // ===== Compatibility helpers used by controllers =====

    /** Simple delegate used by existing validations in controllers/config. */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(safe(email).toLowerCase());
    }

    /** Simple delegate used by existing validations in controllers/config. */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(safe(username));
    }

    // Update: new case-insensitive helpers to satisfy RegisterController usage
    public boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(safe(email));
    }

    public boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(safe(username));
    }

    /**
     * Lookup by username OR email (handy for login).
     */
    public Optional<User> findByEmailOrUsername(String input) {
        final String v = safe(input);
        // Try username (strict), then username (ignore case), then email (ignore case)
        Optional<User> byUser = userRepository.findByUsername(v);
        if (byUser.isPresent()) return byUser;

        byUser = userRepository.findByUsernameIgnoreCase(v); // Update
        if (byUser.isPresent()) return byUser;

        Optional<User> byEmail = userRepository.findByEmailIgnoreCase(v); // Update
        if (byEmail.isPresent()) return byEmail;

        // Fallback: tiny JDBC email query (kept for compatibility, lowercased)
        return jdbcTemplate.query(
                "SELECT id, username, password, email, enabled FROM users WHERE LOWER(email) = ? LIMIT 1",
                rs -> rs.next()
                        ? Optional.of(mapRowToUser(rs.getLong("id"),
                                                   rs.getString("username"),
                                                   rs.getString("password"),
                                                   rs.getString("email"),
                                                   rs.getBoolean("enabled")))
                        : Optional.empty(),
                v.toLowerCase()
        );
    }

    /** Keeps older controller code that calls emulateCreate(dto). */
    public User emulateCreate(UserRegistration dto) {
        return register(safe(dto.getUsername()), safe(dto.getPassword()), safe(dto.getEmail()));
    }

    // Small helper to map a JDBC row to our User model.
    private User mapRowToUser(Long id, String username, String password, String email, boolean enabled) {
        User u = new User(username, password, email, enabled);
        u.setId(id);
        return u;
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
