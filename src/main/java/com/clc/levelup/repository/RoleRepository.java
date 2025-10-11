package com.clc.levelup.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.clc.levelup.model.Role;

/*
 * Team note:
 * Used by registration to look up ROLE_USER.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    /** Find a role by its unique name (e.g., "ROLE_USER"). */
    Optional<Role> findByName(String name);
}
