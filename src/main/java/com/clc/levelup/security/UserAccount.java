package com.clc.levelup.security;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/*
 * Represents a user record in the database.
 */
@Table("users")
public class UserAccount {

  @Id
  private Long id;

  @Column("username")
  private String username;

  @Column("password")
  private String password;

  @Column("enabled")
  private Integer enabled; // 1 = active, 0 = disabled

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public Integer getEnabled() { return enabled; }
  public void setEnabled(Integer enabled) { this.enabled = enabled; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserAccount)) return false;
    UserAccount that = (UserAccount) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() { return Objects.hash(username); }
}
