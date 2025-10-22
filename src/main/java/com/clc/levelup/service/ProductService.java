package com.clc.levelup.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clc.levelup.model.Product;
import com.clc.levelup.repository.ProductRepository;

/*
 * Team note:
 * Thin service layer for products. Controllers call this.
 * If we add rules later (e.g., stock checks), they go here.
 */
@Service
public class ProductService {

    private final ProductRepository repo;

    /** Inject repository (Spring does this for us). */
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /** Get all products for the list page. */
    @Transactional(readOnly = true) // read-only: no writes happen here
    public Iterable<Product> findAll() {
        // No filtering yet; just return everything.
        return repo.findAll();
    }

    /** Get one product by id (used for details and edit). */
    @Transactional(readOnly = true) // read-only lookup
    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    /**
     * Save a new product (used by the create form).
     * @param p product from the form
     * @return saved entity with id
     */
    @Transactional
    public Product create(Product p) {
        // Keep validation on the form/controller for now.
        return repo.save(p);
    }

    /**
     * Update an existing product.
     * Simple existence check so we don't create by mistake.
     */
    @Transactional
    public Product update(Product p) {
        // Use helper so we fail fast if the id is missing or unknown
        requireExists(p.getId(), "Product not found for update.");
        return repo.save(p);
    }

    /**
     * Delete by id with a quick existence check.
     */
    @Transactional
    public void deleteById(Long id) {
        requireExists(id, "Product not found for delete.");
        repo.deleteById(id);
    }

    // --- Helpers ---

    /**
     * Small helper to make existence checks easy to read.
     * Throws IllegalArgumentException with the given message when not found.
     */
    @Transactional(readOnly = true)
    protected void requireExists(Long id, String messageIfMissing) {
        if (id == null || !repo.existsById(id)) {
            throw new IllegalArgumentException(messageIfMissing);
        }
    }
}
