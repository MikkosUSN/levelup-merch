package com.clc.levelup.cart;

import java.math.BigDecimal;

/**
 * Represents a single product line within the shopping cart.
 * Each CartItem tracks the product ID, name, unit price, and quantity.
 * Used by CartService to calculate totals and manage cart contents.
 */
public class CartItem {

    private long productId;
    private String name;
    private BigDecimal price; // Unit price of the product
    private int quantity;

    /**
     * Create a new cart line item.
     * @param productId unique product identifier
     * @param name product name
     * @param price unit price
     * @param quantity quantity (values below 1 are treated as 1)
     */
    public CartItem(long productId, String name, BigDecimal price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        // Guard to prevent invalid quantity
        this.quantity = Math.max(1, quantity);
    }

    /**
     * Get the product ID for this line item.
     * @return product ID
     */
    public long getProductId() {
        return productId;
    }

    /**
     * Get the product name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the unit price.
     * @return unit price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Get the quantity currently set for this item.
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Update the quantity for this item.
     * Quantities less than one default to one.
     * @param quantity new quantity
     */
    public void setQuantity(int quantity) {
        // Ensure at least one item remains in the cart
        this.quantity = Math.max(1, quantity);
    }

    /**
     * Calculate the subtotal for this item (price × quantity).
     * @return subtotal amount
     */
    public BigDecimal getSubtotal() {
        // BigDecimal arithmetic prevents floating point errors
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
