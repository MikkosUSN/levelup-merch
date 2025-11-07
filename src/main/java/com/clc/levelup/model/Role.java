package com.clc.levelup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a security role such as {@code ROLE_USER} or {@code ROLE_ADMIN}.
 * This entity is mapped to the {@code roles} table and used by Spring Security
 * to manage user authorities.
 */
@Table("roles")
public class Role {

    /** Primary key for the role record. */
    @Id
    private Long id;

    /** Role name (for example, ROLE_USER or ROLE_ADMIN). Must be unique. */
    private String name;

    /** Default constructor required by Spring Data. */
    public Role() { }

    /**
     * Convenience constructor for quick seeding or testing.
     * @param name role name to assign
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Get the database ID for this role.
     * @return role ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the database ID for this role.
     * Typically assigned automatically by the persistence layer.
     * @param id role ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the role name (e.g., ROLE_USER).
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the role name.
     * @param name role name value
     */
    public void setName(String name) {
        this.name = name;
    }
}
