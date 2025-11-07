package com.clc.levelup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a user account record mapped to the {@code users} table.
 * This entity supports authentication and registration flows.
 * Passwords should always be stored as securely hashed values
 * (BCrypt is used by the service layer).
 */
@Table("users")
public class User {

    /** Primary key ID for the user record. */
    @Id
    private Long id;

    /** Username chosen by the user (must be unique). */
    @Column("username")
    private String username;

    /** BCrypt-hashed password string stored in the database. */
    @Column("password")
    private String password;

    /** Unique email address associated with this account. */
    @Column("email")
    private String email;

    /** Indicates whether the account is active or disabled. */
    @Column("enabled")
    private boolean enabled;

    /** Default constructor required by Spring Data. */
    public User() { }

    /**
     * Convenience constructor for creating or seeding user records in code.
     * @param username account username
     * @param password hashed password (BCrypt)
     * @param email account email
     * @param enabled true if account should be active
     */
    public User(String username, String password, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    // ----- Getters and Setters -----

    /**
     * Get the user ID.
     * @return unique ID
     */
    public Long getId() { return id; }

    /**
     * Set the user ID.
     * @param id database ID value
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Get the username.
     * @return username string
     */
    public String getUsername() { return username; }

    /**
     * Set the username.
     * @param username account username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Get the stored password hash.
     * @return password hash
     */
    public String getPassword() { return password; }

    /**
     * Set the stored password hash.
     * @param password hashed password string
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Get the user's email address.
     * @return email address
     */
    public String getEmail() { return email; }

    /**
     * Set the user's email address.
     * @param email email address value
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Determine if the account is enabled.
     * @return true if account is active
     */
    public boolean isEnabled() { return enabled; }

    /**
     * Set the enabled flag for the account.
     * @param enabled true if active
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
