-- ===== LevelUp Game Merch Database Structure =====
-- Clean structure for authentication, roles, and products.

DROP TABLE IF EXISTS password_reset_tokens;   -- Team note: ensure drop order handles new table first
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS products;

-- Users table (indexes defined inline)
CREATE TABLE users (
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
CREATE TABLE roles (
  id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(32) NOT NULL,

  UNIQUE KEY uk_roles_name (name),
  KEY idx_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Join table: user_roles
CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Products table (indexes inline)
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

  UNIQUE KEY uk_products_partNumber (partNumber),
  KEY idx_products_name (name),
  KEY idx_products_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Password reset token storage (one-time, expiring) + indexes inline
CREATE TABLE password_reset_tokens (
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

-- Optional admin user seed (replace the BCrypt hash before running)
-- INSERT INTO users (username, password, email, enabled)
-- VALUES ('admin', '$2a$10$REPLACE_WITH_BCRYPT_HASH_____________________', 'admin@example.com', 1)
-- ON DUPLICATE KEY UPDATE email = VALUES(email), enabled = VALUES(enabled);

-- Optional admin role mapping (only if admin user row exists)
-- INSERT IGNORE INTO user_roles (user_id, role_id)
-- SELECT u.id, r.id FROM users u, roles r
--  WHERE u.username='admin' AND r.name='ROLE_ADMIN';
