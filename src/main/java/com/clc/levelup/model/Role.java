package com.clc.levelup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/*
 * Team note:
 * Roles like ROLE_USER / ROLE_ADMIN. We seed ROLE_USER in the DB.
 * Keep this class tiny—just data.
 */
@Table("roles")
public class Role {

    // --- Fields ---
    @Id
    private Long id;      // database identity
    private String name;  // e.g., "ROLE_USER"

    // --- Constructors ---

    /** Required by Spring Data. */
    public Role() { }

    /** Convenience constructor for quick seeds/tests. */
    public Role(String name) {
        this.name = name;
    }

    // --- Getters/Setters ---

    /** Get the DB id. */
    public Long getId() {
        return id;
    }

    /** Set the DB id (set by the framework after save). */
    public void setId(Long id) {
        this.id = id;
    }

    /** Get the role name (e.g., ROLE_USER). */
    public String getName() {
        return name;
    }

    /** Set the role name (must be unique in the table). */
    public void setName(String name) {
        this.name = name;
    }
}
