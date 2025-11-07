package com.clc.levelup.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clc.levelup.dto.LoginRequest;
import com.clc.levelup.model.User;

/**
 * AuthService implementation that authenticates a user using {@link UserService}
 * and a {@link PasswordEncoder}.
 * <p>
 * Used by the simple login flow to validate the demo user and any registered
 * users before Spring Security takes over the full pipeline.
 * </p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the AuthService with required dependencies.
     *
     * @param userService     service used to look up users by email or username
     * @param passwordEncoder encoder used to verify raw passwords against stored hashes
     */
    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verifies login credentials against stored user data.
     * <p>
     * Looks up a user by email or username, then uses {@link PasswordEncoder}
     * to match the submitted password against the stored hash.
     * </p>
     *
     * @param req login form data
     * @return {@code true} if a matching user exists and the password is valid;
     *         {@code false} otherwise
     */
    @Override
    public boolean authenticate(LoginRequest req) {
        String identifier = req.getEmailOrUsername();
        String rawPassword = req.getPassword();

        Optional<User> candidate = userService.findByEmailOrUsername(identifier);
        return candidate.isPresent() && passwordsMatch(candidate.get(), rawPassword);
    }

    /**
     * Returns a principal object representing the authenticated user.
     * <p>
     * Controllers store this value in the HTTP session for display in the UI.
     * </p>
     *
     * @param emailOrUsername identifier entered by the user
     * @return the matching {@link User}, or {@code null} if not found
     */
    @Override
    public Object toPrincipal(String emailOrUsername) {
        return userService.findByEmailOrUsername(emailOrUsername).orElse(null);
    }

    /**
     * Internal helper that compares the submitted raw password with the stored hash.
     *
     * @param user        user record containing the stored password hash
     * @param rawPassword password entered by the user
     * @return {@code true} if the password matches; {@code false} otherwise
     */
    private boolean passwordsMatch(User user, String rawPassword) {
        String stored = user.getPassword();
        // Use PasswordEncoder so this works with BCrypt-hashed passwords
        return stored != null && passwordEncoder.matches(rawPassword, stored);
    }
}
