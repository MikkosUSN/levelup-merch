package com.clc.levelup.cart;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

/*
 * Session cart (one per user session).
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {
    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public void addOrIncrement(CartItem item) {
        CartItem existing = items.get(item.getProductId());
        if (existing == null) {
            items.put(item.getProductId(), item);
        } else {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        }
    }

    public void updateQuantity(long productId, int quantity) {
        CartItem existing = items.get(productId);
        if (existing != null) existing.setQuantity(quantity);
    }

    public void remove(long productId) { items.remove(productId); }

    public List<CartItem> getItems() { return new ArrayList<>(items.values()); }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public void clear() { items.clear(); }
}
