package com.clc.levelup.controllers;

import com.clc.levelup.cart.CartItem;
import com.clc.levelup.cart.CartService;
import com.clc.levelup.model.Product;
import com.clc.levelup.orders.OrderService;
import com.clc.levelup.repository.UserRepository;
import com.clc.levelup.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/*
 * Cart add/update/remove and checkout.
 */
@Controller
public class CartController {

    private final ProductService productService;
    private final CartService cart;
    private final OrderService orders;
    private final UserRepository users;

    public CartController(ProductService productService, CartService cart, OrderService orders, UserRepository users) {
        this.productService = productService;
        this.cart = cart;
        this.orders = orders;
        this.users = users;
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("items", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        return "cart/index";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable long id, @RequestParam(defaultValue = "1") int qty) {
        Product p = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        cart.addOrIncrement(new CartItem(p.getId(), p.getName(), p.getPrice(), Math.max(1, qty)));
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateQty(@PathVariable long id, @RequestParam int qty) {
        cart.updateQuantity(id, Math.max(1, qty));
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable long id) {
        cart.remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        model.addAttribute("items", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        return "checkout/index";
    }

    @PostMapping("/checkout")
    public String doCheckout(Authentication auth, Model model) {
        if (cart.isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("items", cart.getItems());
            model.addAttribute("total", cart.getTotal());
            return "checkout/index";
        }
        Long userId = currentUserId(auth);
        BigDecimal total = cart.getTotal();
        Long orderId = orders.createOrder(userId, cart.getItems(), total);
        cart.clear();
        return "redirect:/orders?success=1";
    }

    // Resolve logged-in user id from Authentication
    private Long currentUserId(Authentication auth) {
        String name = auth.getName(); // Spring Security principal username
        return users.findByUsernameIgnoreCase(name)
                .or(() -> users.findByEmailIgnoreCase(name))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found: " + name));
    }
}
