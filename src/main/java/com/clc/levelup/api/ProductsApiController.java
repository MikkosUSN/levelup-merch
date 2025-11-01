package com.clc.levelup.api;

import com.clc.levelup.model.Product;
import com.clc.levelup.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/*
 * M7 - Products REST API
 * - GET /api/products        -> list all products
 * - GET /api/products/{id}   -> get one product by id
 * Secured via Basic Auth (ROLE_API) configured in SecurityConfig.
 */
@RestController
@RequestMapping("/api/products")
public class ProductsApiController {

    // Connects controller with ProductService
    private final ProductService productService;

    // Constructor injection for ProductService
    public ProductsApiController(ProductService productService) {
        this.productService = productService;
    }

    /*
     * GET /api/products
     * Returns all products from the database.
     * Updated in M7: uses Iterable<Product> to match the service return type.
     */
    @GetMapping
    public ResponseEntity<Iterable<Product>> getAll() {
        // Retrieve all products
        Iterable<Product> items = productService.findAll();

        // If there are no products, return 204 No Content
        if (items == null || !items.iterator().hasNext()) {
            return ResponseEntity.noContent().build();
        }

        // Return 200 OK with the product data
        return ResponseEntity.ok(items);
    }

    /*
     * GET /api/products/{id}
     * Returns a single product by its ID.
     * Example: /api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable long id) {
        // Find the product by its ID
        Optional<Product> found = productService.findById(id);

        // Return 200 OK if found, otherwise 404 Not Found
        return found.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
