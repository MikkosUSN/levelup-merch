package com.clc.levelup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/*
 * Team note:
 * User is for registration/login. We will add security + hashing in M6.
 */
@Table("users")
public class User {

    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password; // plain for M4 only (hash later in M6)

    @Column("email")
    private String email;

    @Column("enabled")
    private boolean enabled;

    /** Framework needs an empty constructor. */
    public User() { }

    /** Simple all-args constructor for quick saves. */
    public User(String username, String password, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    // --- Getters/Setters ---
    /** DB id getter. */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** Login username (must be unique). */
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /** Raw password for M4 (placeholder). */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /** Email address (must be unique). */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** Indicates whether the account is active. */
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
