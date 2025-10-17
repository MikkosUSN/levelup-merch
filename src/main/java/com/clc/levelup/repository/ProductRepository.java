package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.Product;

/*
 * Team note:
 * Spring Data JDBC provides CRUD for Product. We can add finders later.
 * Added common helpers so intent is clear when we call the repo.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findById(Long id);
    Iterable<Product> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
