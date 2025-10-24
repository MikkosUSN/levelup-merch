// src/main/java/com/clc/levelup/security/CustomUserDetailsService.java
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
// Only import Spring Security's User class to avoid collision with your domain model
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clc.levelup.repository.UserRepository;

/*
 * Loads a user by "username OR email" for Spring Security.
 * Also fetches ROLE_* authorities via user_roles -> roles.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository users;
    private final JdbcTemplate jdbc;

    public CustomUserDetailsService(UserRepository users, JdbcTemplate jdbc) {
        this.users = users;
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        String raw = safe(emailOrUsername);

        // Prefer email if it looks like one
        Optional<com.clc.levelup.model.User> userOpt = raw.contains("@")
                ? users.findByEmailIgnoreCase(raw.toLowerCase(Locale.ROOT))
                : users.findByUsernameIgnoreCase(raw);

        // Fallback: try the other way
        if (userOpt.isEmpty()) {
            userOpt = raw.contains("@")
                    ? users.findByUsernameIgnoreCase(raw)
                    : users.findByEmailIgnoreCase(raw.toLowerCase(Locale.ROOT));
        }

        com.clc.levelup.model.User u =
                userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<? extends GrantedAuthority> auth = loadAuthorities(u.getId());

        // Build a Spring Security user object using the hashed password
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(auth)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!u.isEnabled())
                .build();
    }

    // Read roles for a user id and convert to SimpleGrantedAuthority.
    private List<GrantedAuthority> loadAuthorities(Long userId) {
        return jdbc.query(
                "SELECT r.name AS role_name " +
                        "FROM user_roles ur JOIN roles r ON ur.role_id = r.id " +
                        "WHERE ur.user_id = ?",
                (rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("role_name")),
                userId
        );
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
