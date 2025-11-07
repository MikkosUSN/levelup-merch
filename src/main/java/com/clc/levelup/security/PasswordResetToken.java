package com.clc.levelup.security;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Represents a single-use token for password reset requests.
 * Each token is tied to a user and expires after a set duration.
 * Mapped to the {@code password_reset_tokens} table.
 */
@Table("password_reset_tokens")
public class PasswordResetToken {

  /** Primary key for the token record. */
  @Id
  private Long id;

  /** User ID that this token belongs to. */
  @Column("user_id")
  private Long userId;

  /** Randomly generated token string (48-character hex). */
  @Column("token")
  private String token;

  /** Date and time when the token expires. */
  @Column("expires_at")
  private LocalDateTime expiresAt;

  /** Indicates whether the token has been used already. */
  @Column("used")
  private Boolean used;

  /** Timestamp when the token was created (for cleanup and tracking). */
  @Column("created_at")
  private LocalDateTime createdAt;

  /** Get the record ID. */
  public Long getId() { return id; }

  /** Set the record ID. */
  public void setId(Long id) { this.id = id; }

  /** Get the associated user ID. */
  public Long getUserId() { return userId; }

  /** Set the associated user ID. */
  public void setUserId(Long userId) { this.userId = userId; }

  /** Get the token string value. */
  public String getToken() { return token; }

  /** Set the token string value. */
  public void setToken(String token) { this.token = token; }

  /** Get the expiration timestamp. */
  public LocalDateTime getExpiresAt() { return expiresAt; }

  /** Set the expiration timestamp. */
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

  /** Get the used status flag. */
  public Boolean getUsed() { return used; }

  /** Set the used status flag. */
  public void setUsed(Boolean used) { this.used = used; }

  /** Get the creation timestamp. */
  public LocalDateTime getCreatedAt() { return createdAt; }

  /** Set the creation timestamp. */
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  /**
   * Helper method to check if the token has been used.
   * @return true if token is marked as used
   */
  public boolean isUsed() { 
    return Boolean.TRUE.equals(used); 
  }

  /**
   * Helper method to check if the token is expired.
   * @return true if the token is expired
   */
  public boolean isExpired() { 
    return expiresAt != null && expiresAt.isBefore(LocalDateTime.now()); 
  }
}
