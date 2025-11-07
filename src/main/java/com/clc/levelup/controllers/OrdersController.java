package com.clc.levelup.controllers;

import com.clc.levelup.orders.Order;
import com.clc.levelup.orders.OrderService;
import com.clc.levelup.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Displays a user's order history and individual order details.
 * Users must be logged in to access this controller.
 */
@Controller
public class OrdersController {

    private final OrderService orders;
    private final UserRepository users;

    /**
     * Constructor to inject required dependencies.
     * @param orders service used to manage order data
     * @param users repository for resolving the current user
     */
    public OrdersController(OrderService orders, UserRepository users) {
        this.orders = orders;
        this.users = users;
    }

    /**
     * Show all orders for the currently authenticated user.
     * @param auth Spring Security authentication context
     * @param model view model
     * @return orders listing page
     */
    @GetMapping("/orders")
    public String orders(Authentication auth, Model model) {
        // Resolve the logged-in user's ID
        Long userId = users.findByUsernameIgnoreCase(auth.getName())
                .or(() -> users.findByEmailIgnoreCase(auth.getName()))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // Load all orders for this user
        model.addAttribute("orders", orders.findOrdersForUser(userId));

        // Render the orders index page
        return "orders/index";
    }

    /**
     * Show the details for a specific order, including its line items.
     * Ensures that only the owner of the order can view it.
     * @param id order identifier
     * @param auth authentication context
     * @param model view model
     * @return order detail page or redirect if unauthorized
     */
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Authentication auth, Model model) {
        // Resolve the logged-in user's ID
        Long userId = users.findByUsernameIgnoreCase(auth.getName())
                .or(() -> users.findByEmailIgnoreCase(auth.getName()))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // Retrieve the order and its items
        Order order = orders.findOrderWithItems(id);

        // Verify ownership before displaying
        if (!order.getUserId().equals(userId)) {
            // Prevent viewing someone else's order
            return "redirect:/orders?error=unauthorized";
        }

        // Add order and item details to the model
        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());

        // Render the order details view
        return "orders/detail";
    }
}
