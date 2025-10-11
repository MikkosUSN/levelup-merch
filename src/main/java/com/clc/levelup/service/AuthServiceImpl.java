package com.clc.levelup.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.clc.levelup.dto.LoginRequest;
import com.clc.levelup.model.User;

/*
 * Team note:
 * Implementation of our AuthService interface using JDBC-backed UserService.
 * M4: plain text password compare (hashing/Spring Security in M6).
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    /** Spring injects UserService. */
    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    // Minimal auth for M2 (no Spring Security yet).
    @Override
    public boolean authenticate(LoginRequest req) {
        String identifier = req.getEmailOrUsername();
        String rawPassword = req.getPassword();

        Optional<User> candidate = userService.findByEmailOrUsername(identifier);
        return candidate.isPresent() && passwordsMatch(candidate.get(), rawPassword);
    }

    @Override
    public Object toPrincipal(String emailOrUsername) {
        return userService.findByEmailOrUsername(emailOrUsername).orElse(null);
    }

    // --- internal helper ---

    /** Plain text compare for M4. Hashing comes in M6. */
    private boolean passwordsMatch(User user, String rawPassword) {
        String stored = user.getPassword();
        return stored != null && stored.equals(rawPassword);
    }
}
