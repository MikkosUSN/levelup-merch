package com.clc.levelup.controllers;

import com.clc.levelup.orders.Order;
import com.clc.levelup.orders.OrderService;
import com.clc.levelup.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * Shows a user's orders. Empty until the first purchase.
 * M7 update: added /orders/{id} route for viewing order details.
 */
@Controller
public class OrdersController {

    private final OrderService orders;
    private final UserRepository users;

    public OrdersController(OrderService orders, UserRepository users) {
        this.orders = orders;
        this.users = users;
    }

    @GetMapping("/orders")
    public String orders(Authentication auth, Model model) {
        Long userId = users.findByUsernameIgnoreCase(auth.getName())
                .or(() -> users.findByEmailIgnoreCase(auth.getName()))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        model.addAttribute("orders", orders.findOrdersForUser(userId));
        return "orders/index";
    }

    /*
     * M7 update: Display individual order details.
     */
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Authentication auth, Model model) {
        Long userId = users.findByUsernameIgnoreCase(auth.getName())
                .or(() -> users.findByEmailIgnoreCase(auth.getName()))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

        Order order = orders.findOrderWithItems(id);

        // Verify the logged-in user owns the order
        if (!order.getUserId().equals(userId)) {
            return "redirect:/orders?error=unauthorized";
        }

        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "orders/detail";
    }
}
