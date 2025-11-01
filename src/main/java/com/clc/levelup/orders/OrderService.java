package com.clc.levelup.orders;

import com.clc.levelup.cart.CartItem;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Team note:
 * Handles order creation and retrieval.
 * When checkout happens, we insert into 'orders' and 'order_items'.
 * Each new order reduces product stock through DB trigger.
 * M7 update: Added method findOrderWithItems() for detailed view.
 */
@Service
public class OrderService {

    private final JdbcTemplate jdbc;

    /** Inject JdbcTemplate for database access. */
    public OrderService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /*
     * Creates an order record for a user and saves all order_items.
     * Called from CartController during checkout.
     */
    public Long createOrder(Long userId, List<CartItem> cartItems, BigDecimal total) {
        KeyHolder kh = new GeneratedKeyHolder();

        // Insert order header and capture generated ID
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO orders (user_id, created_at, total) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, userId);
            ps.setObject(2, LocalDateTime.now());
            ps.setBigDecimal(3, total);
            return ps;
        }, kh);

        long orderId = kh.getKey().longValue();

        // Insert each item under this order
        for (CartItem ci : cartItems) {
            jdbc.update(
                "INSERT INTO order_items (order_id, product_id, name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)",
                orderId, ci.getProductId(), ci.getName(), ci.getPrice(), ci.getQuantity()
            );
        }

        return orderId;
    }

    /*
     * Retrieves all orders for a given user.
     * Used by OrdersController to list purchase history.
     */
    public List<Order> findOrdersForUser(Long userId) {
        return jdbc.query(
            "SELECT id, user_id, created_at, total FROM orders WHERE user_id = ? ORDER BY created_at DESC",
            (rs, n) -> {
                Order o = new Order();
                o.setId(rs.getLong("id"));
                o.setUserId(rs.getLong("user_id"));
                o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                o.setTotal(rs.getBigDecimal("total"));
                return o;
            },
            userId
        );
    }

    /*
     * M7 update: Load an order with its related order_items.
     * Used for order detail view.
     */
    public Order findOrderWithItems(Long orderId) {
        Order order = jdbc.queryForObject(
            "SELECT id, user_id, created_at, total FROM orders WHERE id = ?",
            (rs, n) -> {
                Order o = new Order();
                o.setId(rs.getLong("id"));
                o.setUserId(rs.getLong("user_id"));
                o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                o.setTotal(rs.getBigDecimal("total"));
                return o;
            },
            orderId
        );

        List<OrderItem> items = jdbc.query(
            "SELECT id, order_id, product_id, name, unit_price, quantity " +
            "FROM order_items WHERE order_id = ?",
            (rs, n) -> {
                OrderItem oi = new OrderItem();
                oi.setId(rs.getLong("id"));
                oi.setOrderId(rs.getLong("order_id"));
                oi.setProductId(rs.getLong("product_id"));
                oi.setName(rs.getString("name"));
                oi.setUnitPrice(rs.getBigDecimal("unit_price"));
                oi.setQuantity(rs.getInt("quantity"));
                return oi;
            },
            orderId
        );

        order.setItems(items);
        return order;
    }
}
