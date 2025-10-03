package com.clc.levelup.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Products landing page after login (mock catalog for M2).
 */
@Controller
public class ProductController {

    @GetMapping("/products")
    public String listProducts(Model model) {
        // Mock items only for Milestone 2
        List<String> products = List.of("T-Shirt", "Hoodie", "Poster", "Keychain");
        model.addAttribute("products", products);
        // templates/products/list.html
        return "products/list";
    }
}
