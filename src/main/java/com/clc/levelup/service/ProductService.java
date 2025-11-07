package com.clc.levelup.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.clc.levelup.model.Product;
import com.clc.levelup.repository.ProductRepository;

/**
 * Service layer for managing {@link Product} data.
 * <p>
 * This class provides a thin abstraction over the {@link ProductRepository}
 * and serves as the main access point for controllers.
 * Any future business rules (such as stock checks or discount logic)
 * can be added here.
 * </p>
 */
@Service
public class ProductService {

    private final ProductRepository repo;

    /**
     * Construct the service and inject the product repository.
     * @param repo repository for product persistence
     */
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * Retrieve all products for display.
     * Read-only transaction for better performance and safety.
     * @return iterable collection of all products
     */
    @Transactional(readOnly = true)
    public Iterable<Product> findAll() {
        return repo.findAll();
    }

    /**
     * Find a single product by its ID.
     * Used for product detail and edit pages.
     * @param id product identifier
     * @return optional containing product if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    /**
     * Create and save a new product.
     * Controllers perform validation before calling this method.
     * @param p product entity to be saved
     * @return the saved product with generated ID
     */
    @Transactional
    public Product create(Product p) {
        return repo.save(p);
    }

    /**
     * Update an existing product.
     * Ensures that the product exists before saving.
     * @param p updated product data
     * @return the saved product entity
     * @throws IllegalArgumentException if product ID is missing or invalid
     */
    @Transactional
    public Product update(Product p) {
        requireExists(p.getId(), "Product not found for update.");
        return repo.save(p);
    }

    /**
     * Delete a product by ID.
     * Performs a pre-check to confirm that the record exists.
     * @param id product identifier
     * @throws IllegalArgumentException if no product exists with the given ID
     */
    @Transactional
    public void deleteById(Long id) {
        requireExists(id, "Product not found for delete.");
        repo.deleteById(id);
    }

    /**
     * Verify that a product exists before performing an operation.
     * Throws an exception if the record is missing.
     * @param id product ID to check
     * @param messageIfMissing exception message if record is not found
     */
    @Transactional(readOnly = true)
    protected void requireExists(Long id, String messageIfMissing) {
        if (id == null || !repo.existsById(id)) {
            throw new IllegalArgumentException(messageIfMissing);
        }
    }
}
