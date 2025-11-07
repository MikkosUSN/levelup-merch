package com.clc.levelup.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.clc.levelup.model.Product;

/**
 * Repository interface for managing {@link Product} entities.
 * Extends Spring Data JDBC {@link CrudRepository} to provide
 * built-in CRUD operations with optional custom finders.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {

    /**
     * Find a product by its ID.
     * @param id product ID
     * @return optional containing the product, or empty if not found
     */
    Optional<Product> findById(Long id);

    /**
     * Retrieve all products from the database.
     * @return iterable list of all products
     */
    Iterable<Product> findAll();

    /**
     * Delete a product by its ID.
     * @param id product ID
     */
    void deleteById(Long id);

    /**
     * Check whether a product exists by its ID.
     * @param id product ID
     * @return true if the product exists, false otherwise
     */
    boolean existsById(Long id);
}
