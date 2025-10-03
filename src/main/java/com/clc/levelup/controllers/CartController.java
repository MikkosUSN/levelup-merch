package com.clc.levelup.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple cart page with mock data (no DB yet).
 */
@Controller
public class CartController {

    @GetMapping("/cart")
    public String viewCart(Model model) {
        // Mock items only for Milestone 2
        List<Map<String, Object>> items = List.of(
            Map.of("name", "T-Shirt", "qty", 2, "price", 19.99),
            Map.of("name", "Hoodie", "qty", 1, "price", 39.99)
        );
        model.addAttribute("items", items);
        // Looks for templates/cart/cart.html
        return "cart/cart";
    }
}
