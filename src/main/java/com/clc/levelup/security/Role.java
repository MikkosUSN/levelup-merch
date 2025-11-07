package com.clc.levelup.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Represents a security role (e.g., {@code ROLE_USER} or {@code ROLE_ADMIN}).
 * Used by Spring Security to determine access permissions.
 * Mapped to the {@code roles} table.
 */
@Table("roles")
public class Role {

  /** Primary key identifier for the role. */
  @Id
  private Long id;

  /** Unique role name used by Spring Security (e.g., ROLE_USER). */
  @Column("name")
  private String name;

  /**
   * Get the role ID.
   * @return role ID
   */
  public Long getId() { return id; }

  /**
   * Set the role ID.
   * @param id ID value
   */
  public void setId(Long id) { this.id = id; }

  /**
   * Get the role name.
   * @return role name
   */
  public String getName() { return name; }

  /**
   * Set the role name.
   * @param name role name value
   */
  public void setName(String name) { this.name = name; }
}
