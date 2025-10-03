package com.clc.levelup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Checkout page stub for M2 (total is mocked).
 */
@Controller
public class CheckoutController {
	// Shows the checkout page with a mock total
    @GetMapping("/checkout")
    public String checkout(Model model) {
        // Mock total only for M2 demo
        model.addAttribute("total", 79.97);
        // templates/checkout/checkout.html
        return "checkout/checkout";
    }
}
