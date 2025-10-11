package com.clc.levelup.service;

import org.springframework.stereotype.Service;

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

    /**
     * Save a new product (used by the create form).
     * @param p product from the form
     * @return saved entity with id
     */
    public Product create(Product p) {
        // In M4 we trust the form; validation is already on the DTO.
        return repo.save(p);
    }
}
