package com.clc.levelup.orders;

import java.math.BigDecimal;

/**
 * Represents an individual line item within an order.
 * Contains product reference, quantity, and pricing details.
 * Used primarily for displaying order summaries and order details in the UI.
 */
public class OrderItem {

    /** Unique identifier for this line item. */
    private Long id;

    /** ID of the order this item belongs to. */
    private Long orderId;

    /** Product ID for the purchased item. */
    private Long productId;

    /** Name of the product at the time of purchase. */
    private String name;

    /** Unit price of the product at the time of purchase. */
    private BigDecimal unitPrice;

    /** Quantity purchased for this item. */
    private int quantity;

    /**
     * Get the unique ID of this line item.
     * @return item ID
     */
    public Long getId() { return id; }

    /**
     * Set the unique ID of this line item.
     * @param id item ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Get the ID of the order that this line item belongs to.
     * @return order ID
     */
    public Long getOrderId() { return orderId; }

    /**
     * Set the ID of the order that this line item belongs to.
     * @param orderId order ID value
     */
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    /**
     * Get the product ID for the associated item.
     * @return product ID
     */
    public Long getProductId() { return productId; }

    /**
     * Set the product ID for the associated item.
     * @param productId product ID value
     */
    public void setProductId(Long productId) { this.productId = productId; }

    /**
     * Get the product name for display.
     * @return product name
     */
    public String getName() { return name; }

    /**
     * Set the product name for this line item.
     * @param name product name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get the unit price for this product.
     * @return unit price
     */
    public BigDecimal getUnitPrice() { return unitPrice; }

    /**
     * Set the unit price for this product.
     * @param unitPrice price per unit
     */
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    /**
     * Get the quantity purchased.
     * @return number of units
     */
    public int getQuantity() { return quantity; }

    /**
     * Set the quantity purchased.
     * @param quantity number of units
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
