package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.Role;

/**
 * Repository interface for accessing {@link Role} data.
 * Provides CRUD operations and a finder for locating roles by name.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    /**
     * Find a role by its unique name (for example, ROLE_USER or ROLE_ADMIN).
     * @param name role name
     * @return optional containing the matching role, or empty if not found
     */
    Optional<Role> findByName(String name);
}
