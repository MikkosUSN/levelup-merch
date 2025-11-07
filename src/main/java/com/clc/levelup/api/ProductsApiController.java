package com.clc.levelup.api;

import com.clc.levelup.model.Product;
import com.clc.levelup.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST endpoints for product data.
 * Endpoints:
 *  - GET /api/products        : list all products
 *  - GET /api/products/{id}   : get one product by id
 * Security: protected via HTTP Basic per SecurityConfig.
 */
@RestController
@RequestMapping("/api/products")
public class ProductsApiController {

    // Connect the controller to the product service layer
    private final ProductService productService;

    /**
     * Constructor injection for ProductService.
     * @param productService service providing product operations
     */
    public ProductsApiController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Return all products. Responds 204 when none exist.
     * @return 200 with products or 204 when empty
     */
    @GetMapping
    public ResponseEntity<Iterable<Product>> getAll() {
        // Query all products from the service
        Iterable<Product> items = productService.findAll();

        // If there are no products, return 204 No Content
        if (items == null || !items.iterator().hasNext()) {
            return ResponseEntity.noContent().build();
        }

        // Return 200 OK with the product data
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/products/{id}
     * Return a single product by id.
     * @param id product identifier
     * @return 200 with product, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable long id) {
        // Look up the product by id
        Optional<Product> found = productService.findById(id);

        // Return 200 OK if found, otherwise 404 Not Found
        return found.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
