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

/**
 * Manages order creation and retrieval.
 * - Inserts a new order header and its line items at checkout time.
 * - Provides queries for a user's order history and a single order with items.
 * Database triggers can be used to adjust product stock after inserts.
 */
@Service
public class OrderService {

    private final JdbcTemplate jdbc;

    /**
     * Inject JdbcTemplate for database access.
     * @param jdbc configured JdbcTemplate
     */
    public OrderService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Create an order for the given user and persist all line items.
     * Called from the checkout flow.
     * @param userId current user's ID
     * @param cartItems items to convert into order lines
     * @param total total amount for the order
     * @return generated order ID
     */
    public Long createOrder(Long userId, List<CartItem> cartItems, BigDecimal total) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

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
        }, keyHolder);

        // Safety check: ensure we received a generated key
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Order ID was not generated.");
        }
        long orderId = key.longValue();

        // Insert each order line
        for (CartItem ci : cartItems) {
            jdbc.update(
                "INSERT INTO order_items (order_id, product_id, name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)",
                orderId, ci.getProductId(), ci.getName(), ci.getPrice(), ci.getQuantity()
            );
        }

        return orderId;
    }

    /**
     * Retrieve all orders for a specific user, newest first.
     * Used to display purchase history.
     * @param userId user ID
     * @return list of orders
     */
    public List<Order> findOrdersForUser(Long userId) {
        return jdbc.query(
            "SELECT id, user_id, created_at, total " +
            "FROM orders WHERE user_id = ? ORDER BY created_at DESC",
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

    /**
     * Load a single order and its related line items.
     * Used by the order details page.
     * @param orderId order ID to load
     * @return order with populated items
     */
    public Order findOrderWithItems(Long orderId) {
        // Query header
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

        // Query items
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

        // Attach items to header
        order.setItems(items);
        return order;
    }
}
