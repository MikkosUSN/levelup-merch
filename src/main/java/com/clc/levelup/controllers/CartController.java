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

/**
 * Manages shopping cart and checkout operations.
 * Handles adding, updating, removing items, and completing checkout.
 */
@Controller
public class CartController {

    private final ProductService productService;
    private final CartService cart;
    private final OrderService orders;
    private final UserRepository users;

    /**
     * Constructor that injects required services.
     * @param productService service providing product data
     * @param cart session-based cart service
     * @param orders order processing service
     * @param users repository for user lookup
     */
    public CartController(ProductService productService, CartService cart, OrderService orders, UserRepository users) {
        this.productService = productService;
        this.cart = cart;
        this.orders = orders;
        this.users = users;
    }

    /**
     * Display the current user's cart.
     * @param model view model
     * @return cart page template
     */
    @GetMapping("/cart")
    public String viewCart(Model model) {
        // Populate cart items and total for display
        model.addAttribute("items", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        return "cart/index";
    }

    /**
     * Add a product to the cart.
     * @param id product identifier
     * @param qty quantity to add (defaults to 1)
     * @return redirect to the cart view
     */
    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable long id, @RequestParam(defaultValue = "1") int qty) {
        // Validate and locate product
        Product p = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        // Add or increment quantity for this product
        cart.addOrIncrement(new CartItem(p.getId(), p.getName(), p.getPrice(), Math.max(1, qty)));

        // Redirect user back to the cart
        return "redirect:/cart";
    }

    /**
     * Update quantity of a product in the cart.
     * @param id product identifier
     * @param qty new quantity value
     * @return redirect to the cart view
     */
    @PostMapping("/cart/update/{id}")
    public String updateQty(@PathVariable long id, @RequestParam int qty) {
        // Ensure at least one item remains
        cart.updateQuantity(id, Math.max(1, qty));
        return "redirect:/cart";
    }

    /**
     * Remove a product from the cart entirely.
     * @param id product identifier
     * @return redirect to the cart view
     */
    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable long id) {
        cart.remove(id);
        return "redirect:/cart";
    }

    /**
     * Show the checkout page with current cart contents.
     * @param model view model
     * @return checkout page template
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        model.addAttribute("items", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        return "checkout/index";
    }

    /**
     * Process checkout and create an order.
     * @param auth current authentication context
     * @param model view model
     * @return redirect to orders page or redisplay on error
     */
    @PostMapping("/checkout")
    public String doCheckout(Authentication auth, Model model) {
        // Block checkout if cart is empty
        if (cart.isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("items", cart.getItems());
            model.addAttribute("total", cart.getTotal());
            return "checkout/index";
        }

        // Resolve logged-in user ID and process the order
        Long userId = currentUserId(auth);
        BigDecimal total = cart.getTotal();
        Long orderId = orders.createOrder(userId, cart.getItems(), total);

        // Clear cart after successful order creation
        cart.clear();

        // Redirect to orders page with success flag
        return "redirect:/orders?success=1";
    }

    /**
     * Resolve the current logged-in user's ID from Authentication.
     * @param auth authentication object from Spring Security
     * @return user ID
     */
    private Long currentUserId(Authentication auth) {
        String name = auth.getName(); // Spring Security principal username

        // Attempt to match username or email to a user record
        return users.findByUsernameIgnoreCase(name)
                .or(() -> users.findByEmailIgnoreCase(name))
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found: " + name));
    }
}
