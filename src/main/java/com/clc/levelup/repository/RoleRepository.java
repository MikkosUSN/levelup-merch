package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.Role;

/*
 * Repository for accessing Role data.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    /** Find role by unique name such as ROLE_USER or ROLE_ADMIN. */
    Optional<Role> findByName(String name);
}
