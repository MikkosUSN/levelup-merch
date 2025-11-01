package com.clc.levelup.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Order header entity (in-memory aggregate for view).
 */
public class Order {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private List<OrderItem> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
