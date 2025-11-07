package com.clc.levelup.security;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Represents a user account record used by the security layer.
 * <p>
 * Mapped to the {@code users} table and referenced during authentication.
 * Stores username, password hash, and enabled status.
 * </p>
 */
@Table("users")
public class UserAccount {

  /** Primary key for the user record. */
  @Id
  private Long id;

  /** Unique username used for login. */
  @Column("username")
  private String username;

  /** Hashed password (BCrypt format). */
  @Column("password")
  private String password;

  /** Account status: 1 for active, 0 for disabled. */
  @Column("enabled")
  private Integer enabled;

  /** Get the user ID. */
  public Long getId() { return id; }

  /** Set the user ID. */
  public void setId(Long id) { this.id = id; }

  /** Get the username. */
  public String getUsername() { return username; }

  /** Set the username. */
  public void setUsername(String username) { this.username = username; }

  /** Get the hashed password. */
  public String getPassword() { return password; }

  /** Set the hashed password. */
  public void setPassword(String password) { this.password = password; }

  /** Get the enabled status (1 for active, 0 for disabled). */
  public Integer getEnabled() { return enabled; }

  /** Set the enabled status. */
  public void setEnabled(Integer enabled) { this.enabled = enabled; }

  /**
   * Equality check based on username, since it must be unique.
   * @param o object to compare
   * @return true if usernames match
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserAccount)) return false;
    UserAccount that = (UserAccount) o;
    return Objects.equals(username, that.username);
  }

  /**
   * Hash code derived from username to match equals().
   * @return hash of username
   */
  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
