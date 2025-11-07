package com.clc.levelup.security;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clc.levelup.repository.UserRepository;

/**
 * Custom implementation of {@link UserDetailsService} for Spring Security.
 * <p>
 * Loads user details by either username or email and resolves their
 * {@code ROLE_*} authorities from the database.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository users;
    private final JdbcTemplate jdbc;

    /**
     * Constructor for dependency injection.
     * @param users repository for user lookups
     * @param jdbc JDBC template used for role queries
     */
    public CustomUserDetailsService(UserRepository users, JdbcTemplate jdbc) {
        this.users = users;
        this.jdbc = jdbc;
    }

    /**
     * Load user details for authentication by username or email.
     * Supports flexible login input (email or username in the same field).
     * @param emailOrUsername login identifier
     * @return populated {@link UserDetails} for authentication
     * @throws UsernameNotFoundException if no matching user is found
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Log attempt for troubleshooting and audit
        log.info("Attempting authentication for identifier: {}", emailOrUsername);

        String raw = safe(emailOrUsername);

        // Determine whether the input is likely an email
        Optional<com.clc.levelup.model.User> userOpt = raw.contains("@")
                ? users.findByEmailIgnoreCase(raw.toLowerCase(Locale.ROOT))
                : users.findByUsernameIgnoreCase(raw);

        // Fallback: if not found, try the alternate field
        if (userOpt.isEmpty()) {
            userOpt = raw.contains("@")
                    ? users.findByUsernameIgnoreCase(raw)
                    : users.findByEmailIgnoreCase(raw.toLowerCase(Locale.ROOT));
        }

        // Throw exception if no match is found
        com.clc.levelup.model.User u =
                userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Load all roles associated with this user
        Collection<? extends GrantedAuthority> authorities = loadAuthorities(u.getId());

        // Build Spring Security's UserDetails object with hashed password
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!u.isEnabled())
                .build();
    }

    /**
     * Retrieve user roles and map them to {@link SimpleGrantedAuthority} objects.
     * @param userId ID of the user whose roles should be loaded
     * @return list of granted authorities
     */
    private List<GrantedAuthority> loadAuthorities(Long userId) {
        return jdbc.query(
                "SELECT r.name AS role_name " +
                        "FROM user_roles ur JOIN roles r ON ur.role_id = r.id " +
                        "WHERE ur.user_id = ?",
                (rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("role_name")),
                userId
        );
    }

    /**
     * Trim and sanitize a string to prevent null references.
     * @param s input string
     * @return safe, trimmed string (never null)
     */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
