package com.clc.levelup.cart;

import java.math.BigDecimal;

/*
 * Line item stored in the cart.
 */
public class CartItem {
    private long productId;
    private String name;
    private BigDecimal price; // unit price
    private int quantity;

    public CartItem(long productId, String name, BigDecimal price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = Math.max(1, quantity);
    }

    public long getProductId() { return productId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
