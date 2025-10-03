package com.clc.levelup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Checkout page for M3
 */
@Controller
public class CheckoutController {

    // show checkout page with total
    @GetMapping("/checkout")
    public String checkout(Model model) {
        // M3: no cart logic yet, so always show $0.00
        BigDecimal total = BigDecimal.ZERO;

        // format number as US currency
        NumberFormat usd = NumberFormat.getCurrencyInstance(Locale.US);
        model.addAttribute("total", total);                 // raw value
        model.addAttribute("totalDisplay", usd.format(total)); // "$0.00"

        // templates/checkout/checkout.html
        return "checkout/checkout";
    }
}
