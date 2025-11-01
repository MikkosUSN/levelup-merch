-- ===== LevelUp Game Merch Database Structure =====
-- Clean structure for authentication, roles, and products.

-- Update (M7): Removed DROP TABLE statements for users, roles, and products
-- so data persists between restarts. These lines were commented out intentionally.
-- DROP TABLE IF EXISTS password_reset_tokens;
-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS roles;
-- DROP TABLE IF EXISTS products;

-- Users table (indexes defined inline)
CREATE TABLE IF NOT EXISTS users (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  username     VARCHAR(64)  NOT NULL,
  password     VARCHAR(128) NOT NULL,
  email        VARCHAR(128) NOT NULL,
  enabled      TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email    (email),
  KEY idx_users_username (username),
  KEY idx_users_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Roles table (index inline)
CREATE TABLE IF NOT EXISTS roles (
  id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(32) NOT NULL,

  UNIQUE KEY uk_roles_name (name),
  KEY idx_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Join table: user_roles
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Products table (indexes inline)
CREATE TABLE IF NOT EXISTS products (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(128) NOT NULL,
  description   VARCHAR(512) NOT NULL,
  manufacturer  VARCHAR(128) NOT NULL,
  category      VARCHAR(64)  NOT NULL,
  partNumber    VARCHAR(64)  NOT NULL,
  quantity      INT          NOT NULL DEFAULT 0,
  price         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_products_partNumber (partNumber),
  KEY idx_products_name (name),
  KEY idx_products_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Password reset token storage (one-time, expiring)
CREATE TABLE IF NOT EXISTS password_reset_tokens (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT       NOT NULL,
  token      VARCHAR(64)  NOT NULL,
  expires_at DATETIME     NOT NULL,
  used       TINYINT(1)   NOT NULL DEFAULT 0,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_prt_token (token),
  KEY idx_prt_user_id (user_id),
  KEY idx_prt_expires_used (expires_at, used),
  CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default roles (idempotent seeds)
INSERT INTO roles (name) VALUES ('ROLE_USER')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Update (M7): ensure ROLE_API exists for secured REST endpoints
INSERT INTO roles (name) VALUES ('ROLE_API')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Optional admin user seed (replace the BCrypt hash before running)
-- INSERT INTO users (username, password, email, enabled)
-- VALUES ('admin', '$2a$10$REPLACE_WITH_BCRYPT_HASH_____________________', 'admin@example.com', 1)
-- ON DUPLICATE KEY UPDATE email = VALUES(email), enabled = VALUES(enabled);

-- Optional admin role mapping (only if admin user row exists)
-- INSERT IGNORE INTO user_roles (user_id, role_id)
-- SELECT u.id, r.id FROM users u, roles r
--  WHERE u.username='admin' AND r.name='ROLE_ADMIN';

-- Optional dedicated API user for testing
-- INSERT INTO users (username, password, email, enabled)
-- VALUES ('api_user', '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH_____________', 'api@example.com', 1)
-- ON DUPLICATE KEY UPDATE email = VALUES(email), enabled = VALUES(enabled);

-- Map that API user to ROLE_API
-- INSERT IGNORE INTO user_roles (user_id, role_id)
-- SELECT u.id, r.id FROM users u, roles r
-- WHERE u.username='api_user' AND r.name='ROLE_API';

-- Update (M7): Added orders and order_items tables for cart + checkout
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  KEY idx_orders_user_id (user_id),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  KEY idx_order_items_order_id (order_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Update (M7): trigger to decrement inventory after each order is placed
DROP TRIGGER IF EXISTS trg_after_order_item_insert;

CREATE TRIGGER trg_after_order_item_insert
AFTER INSERT ON order_items
FOR EACH ROW
UPDATE products
   SET quantity = quantity - NEW.quantity
 WHERE id = NEW.product_id
   AND quantity >= NEW.quantity;

-- Update (M7): safe fallback check for any quantity below zero
UPDATE products SET quantity = 0 WHERE quantity < 0;
