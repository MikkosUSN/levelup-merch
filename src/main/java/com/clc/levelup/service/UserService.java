package com.clc.levelup.service;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.model.Role;
import com.clc.levelup.model.User;
import com.clc.levelup.repository.RoleRepository;
import com.clc.levelup.repository.UserRepository;

/**
 * Handles user registration, lookups, and account validation.
 * <p>
 * This service manages new user creation and attaches default roles.
 * Controllers and authentication services depend on this class for
 * all user-related operations.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a {@code UserService} with repositories and helpers injected by Spring.
     * @param userRepository user data repository
     * @param roleRepository role data repository
     * @param jdbcTemplate used for user-role join inserts
     * @param passwordEncoder encoder for hashing passwords
     */
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JdbcTemplate jdbcTemplate,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user account.
     * <p>Steps:</p>
     * <ol>
     *   <li>Normalize and validate username and email.</li>
     *   <li>Check for duplicates (case-insensitive).</li>
     *   <li>Hash the password before saving.</li>
     *   <li>Assign the default {@code ROLE_USER} role.</li>
     * </ol>
     * @param username desired username
     * @param rawPassword plain text password to be hashed
     * @param email user email address
     * @return saved {@link User} record
     * @throws IllegalArgumentException if username or email already exists
     */
    public User register(String username, String rawPassword, String email) {
        final String normUsername = safe(username);
        final String normEmail = safe(email).toLowerCase();

        // Prevent duplicates
        if (existsByUsernameIgnoreCase(normUsername)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (existsByEmailIgnoreCase(normEmail)) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Hash password and save
        String hashed = passwordEncoder.encode(rawPassword);
        User saved = userRepository.save(new User(normUsername, hashed, normEmail, true));

        // Fetch ROLE_USER
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER missing in roles table"));

        // Assign role to new user
        jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)",
                saved.getId(), roleUser.getId()
        );

        return saved;
    }

    /**
     * Find a user by username (case-sensitive).
     * @param username the username to search for
     * @return optional containing user if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(safe(username));
    }

    // ===== Validation Helpers =====

    /**
     * Check if an email already exists.
     * @param email email to check
     * @return true if the email is already used
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(safe(email).toLowerCase());
    }

    /**
     * Check if a username already exists.
     * @param username username to check
     * @return true if the username already exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(safe(username));
    }

    /**
     * Case-insensitive email check used by registration forms.
     * @param email email address
     * @return true if email exists (ignoring case)
     */
    public boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(safe(email));
    }

    /**
     * Case-insensitive username check used by registration forms.
     * @param username username
     * @return true if username exists (ignoring case)
     */
    public boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(safe(username));
    }

    /**
     * Find a user by either username or email (case-insensitive).
     * Used for login and password reset.
     * @param input username or email
     * @return optional containing matching user, if found
     */
    public Optional<User> findByEmailOrUsername(String input) {
        final String v = safe(input);

        Optional<User> byUser = userRepository.findByUsername(v);
        if (byUser.isPresent()) return byUser;

        byUser = userRepository.findByUsernameIgnoreCase(v);
        if (byUser.isPresent()) return byUser;

        Optional<User> byEmail = userRepository.findByEmailIgnoreCase(v);
        if (byEmail.isPresent()) return byEmail;

        // Fallback: small direct JDBC query for compatibility
        return jdbcTemplate.query(
                "SELECT id, username, password, email, enabled FROM users WHERE LOWER(email) = ? LIMIT 1",
                rs -> rs.next()
                        ? Optional.of(mapRowToUser(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getBoolean("enabled")))
                        : Optional.empty(),
                v.toLowerCase()
        );
    }

    /**
     * Legacy helper that mimics earlier registration logic for DTO-based calls.
     * @param dto user registration data
     * @return saved user entity
     */
    public User emulateCreate(UserRegistration dto) {
        return register(safe(dto.getUsername()), safe(dto.getPassword()), safe(dto.getEmail()));
    }

    // ===== Internal Helpers =====

    /** Convert a JDBC row into a {@link User} model object. */
    private User mapRowToUser(Long id, String username, String password, String email, boolean enabled) {
        User u = new User(username, password, email, enabled);
        u.setId(id);
        return u;
    }

    /** Trim a string and return an empty value if null. */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
