-- ===== LevelUp Game Merch Database Structure =====
-- Clean structure for authentication, roles, and products.

DROP TABLE IF EXISTS password_reset_tokens;   -- Update (M6): ensure drop order handles new table first
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS products;

-- Users table
CREATE TABLE users (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  username     VARCHAR(64)  NOT NULL UNIQUE,
  password     VARCHAR(128) NOT NULL,
  email        VARCHAR(128) NOT NULL UNIQUE,
  enabled      TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;  -- Update (M6): explicit engine/charset for consistency

-- Roles table
CREATE TABLE roles (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(32) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;  -- Update (M6): explicit engine/charset

-- Join table: user_roles
CREATE TABLE user_roles (
  user_id      BIGINT NOT NULL,
  role_id      BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;  -- Update (M6): explicit engine/charset

-- Products table
CREATE TABLE products (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(128) NOT NULL,
  description   VARCHAR(512) NOT NULL,
  manufacturer  VARCHAR(128) NOT NULL,
  category      VARCHAR(64)  NOT NULL,
  partNumber    VARCHAR(64)  NOT NULL,
  quantity      INT          NOT NULL DEFAULT 0,
  price         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_products_partNumber (partNumber)  -- Update (M6): keep part numbers unique
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;  -- Update (M6): explicit engine/charset

-- Update (M6): password reset token storage (one-time, expiring)
CREATE TABLE password_reset_tokens (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id      BIGINT       NOT NULL,
  token        VARCHAR(64)  NOT NULL UNIQUE,
  expires_at   DATETIME     NOT NULL,
  used         TINYINT(1)   NOT NULL DEFAULT 0,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Update (M6): helpful indexes for lookup performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_prt_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_prt_expires_used ON password_reset_tokens(expires_at, used);

-- Default role for new users
INSERT INTO roles (name) VALUES ('ROLE_USER')
ON DUPLICATE KEY UPDATE name = VALUES(name);  -- Update (M6): idempotent seed

-- Update (M6): optional seed for ROLE_ADMIN (safe if re-run)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Update (M6): optional admin user seed (replace the BCrypt hash before running)
-- INSERT INTO users (username, password, email, enabled)
-- VALUES ('admin', '$2a$10$REPLACE_WITH_BCRYPT_HASH_____________________', 'admin@example.com', 1)
-- ON DUPLICATE KEY UPDATE email = VALUES(email), enabled = VALUES(enabled);

-- Update (M6): optional admin role mapping (only if admin user row exists)
-- INSERT IGNORE INTO user_roles (user_id, role_id)
-- SELECT u.id, r.id FROM users u, roles r
--  WHERE u.username='admin' AND r.name='ROLE_ADMIN';
