package com.clc.levelup.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order placed by a user.
 * Acts as an in-memory aggregate for order details displayed in the UI.
 * Typically composed of an order header and a list of line items.
 */
public class Order {

    /** Unique identifier for the order. */
    private Long id;

    /** ID of the user who placed the order. */
    private Long userId;

    /** Timestamp when the order was created. */
    private LocalDateTime createdAt;

    /** Total amount for this order. */
    private BigDecimal total;

    /** List of line items belonging to this order. */
    private List<OrderItem> items;

    /**
     * Get the order ID.
     * @return order ID
     */
    public Long getId() { return id; }

    /**
     * Set the order ID.
     * @param id order ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Get the user ID associated with this order.
     * @return user ID
     */
    public Long getUserId() { return userId; }

    /**
     * Set the user ID for this order.
     * @param userId user ID value
     */
    public void setUserId(Long userId) { this.userId = userId; }

    /**
     * Get the date and time when the order was created.
     * @return timestamp of creation
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Set the creation date and time.
     * @param createdAt timestamp value
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Get the total amount for this order.
     * @return total as BigDecimal
     */
    public BigDecimal getTotal() { return total; }

    /**
     * Set the total amount for this order.
     * @param total total value
     */
    public void setTotal(BigDecimal total) { this.total = total; }

    /**
     * Get the list of items included in this order.
     * @return list of order items
     */
    public List<OrderItem> getItems() { return items; }

    /**
     * Set the list of items for this order.
     * @param items list of order items
     */
    public void setItems(List<OrderItem> items) { this.items = items; }
}
