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
    public Iterable<Product> findAll() {
        // No filtering yet; just return everything.
        return repo.findAll();
    }

    /** Get one product by id (used for details and edit). */
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
        if (p.getId() == null || !repo.existsById(p.getId())) {
            throw new IllegalArgumentException("Product not found for update.");
        }
        return repo.save(p);
    }

    /**
     * Delete by id with a quick existence check.
     */
    @Transactional
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Product not found for delete.");
        }
        repo.deleteById(id);
    }
}
