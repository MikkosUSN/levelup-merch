package com.clc.levelup.security;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/*
 * One-time token for password reset.
 */
@Table("password_reset_tokens")
public class PasswordResetToken {

  @Id
  private Long id;

  @Column("user_id")
  private Long userId;

  @Column("token")
  private String token;

  @Column("expires_at")
  private LocalDateTime expiresAt;

  @Column("used")
  private Boolean used; // Update (M6): map to Boolean to match JDBC result for TINYINT(1)

  // Update (M6): track when token was created (for cleanup and logging)
  @Column("created_at")
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

  public Boolean getUsed() { return used; }
  public void setUsed(Boolean used) { this.used = used; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  // Update (M6): helpers keep calling code tidy
  public boolean isUsed() { return Boolean.TRUE.equals(used); }
  public boolean isExpired() { return expiresAt != null && expiresAt.isBefore(LocalDateTime.now()); }
}
