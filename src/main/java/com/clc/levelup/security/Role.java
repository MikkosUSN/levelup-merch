package com.clc.levelup.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/*
 * Represents a role such as ROLE_USER or ROLE_ADMIN.
 */
@Table("roles")
public class Role {

  @Id
  private Long id;

  @Column("name")
  private String name;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
}
