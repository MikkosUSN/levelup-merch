package com.clc.levelup.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Orders list stub for M2 (mock data).
 */
@Controller
public class OrderController {

    @GetMapping("/orders")
    public String listOrders(Model model) {
        // Mock orders only for demo
        List<Map<String, Object>> orders = List.of(
            Map.of("id", 1001, "date", "2025-09-27", "total", 59.99),
            Map.of("id", 1002, "date", "2025-09-26", "total", 89.49)
        );
        model.addAttribute("orders", orders);
        // templates/orders/list.html
        return "orders/list";
    }
}
