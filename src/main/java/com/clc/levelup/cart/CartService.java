package com.clc.levelup.cart;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages the user's shopping cart for the current web session.
 * Each session has its own CartService instance.
 * Provides methods to add, update, and remove CartItem objects
 * and to calculate the running total for checkout.
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    // Key: productId, Value: corresponding CartItem
    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    /**
     * Add a new product to the cart or increase its quantity if it already exists.
     * @param item item to add or increment
     */
    public void addOrIncrement(CartItem item) {
        CartItem existing = items.get(item.getProductId());
        if (existing == null) {
            // New item — add it to the map
            items.put(item.getProductId(), item);
        } else {
            // Existing item — just increase quantity
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        }
    }

    /**
     * Update the quantity for a specific product line.
     * @param productId product identifier
     * @param quantity new quantity value
     */
    public void updateQuantity(long productId, int quantity) {
        CartItem existing = items.get(productId);
        if (existing != null) {
            existing.setQuantity(quantity);
        }
    }

    /**
     * Remove a product line from the cart.
     * @param productId product identifier to remove
     */
    public void remove(long productId) {
        items.remove(productId);
    }

    /**
     * Get a list of all CartItems currently in the cart.
     * @return list of CartItem objects
     */
    public List<CartItem> getItems() {
        // Return a copy to avoid exposing internal map
        return new ArrayList<>(items.values());
    }

    /**
     * Calculate the total price for all items in the cart.
     * @return total amount
     */
    public BigDecimal getTotal() {
        // Sum all subtotals using BigDecimal for accurate math
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if the cart is empty.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Clear all items from the cart.
     */
    public void clear() {
        items.clear();
    }
}
