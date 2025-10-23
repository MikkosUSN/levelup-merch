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
  private Integer used; // 0 = not used, 1 = used

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

  public Integer getUsed() { return used; }
  public void setUsed(Integer used) { this.used = used; }
}
