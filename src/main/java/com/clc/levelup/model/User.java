// src/main/java/com/clc/levelup/model/User.java
package com.clc.levelup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/*
 * User entity used for registration and login.
 * Note: password should be stored as a hash (handled by service).
 */
@Table("users")
public class User {

    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password; // team note: this should be a BCrypt hash in production

    @Column("email")
    private String email;

    @Column("enabled")
    private boolean enabled;

    /** Required empty constructor for the framework. */
    public User() { }

    /** Convenience constructor for creating users in code/tests. */
    public User(String username, String password, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    // --- Getters/Setters ---
    /** DB id getter/setter. */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** Login username (unique). */
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /** Password hash (BCrypt). */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /** Email address (unique). */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** Whether the account is active. */
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // Update: small helpers for normalization can be handled in service,
    // keeping entity simple on purpose.
}
