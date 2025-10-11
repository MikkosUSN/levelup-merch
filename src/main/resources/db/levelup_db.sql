-- ===== LevelUp Game Merch - Milestone 4 DDL =====
-- Schema: levelup_db

DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS products;

-- Users
CREATE TABLE users (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  username     VARCHAR(64)  NOT NULL UNIQUE,
  password     VARCHAR(128) NOT NULL,
  email        VARCHAR(128) NOT NULL UNIQUE,
  enabled      TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Roles
CREATE TABLE roles (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(32) NOT NULL UNIQUE
);

-- Join table (many-to-many users <-> roles)
CREATE TABLE user_roles (
  user_id      BIGINT NOT NULL,
  role_id      BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Products
CREATE TABLE products (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(128) NOT NULL,
  description   VARCHAR(512) NOT NULL,
  manufacturer  VARCHAR(128) NOT NULL,           -- keep brand/maker
  category      VARCHAR(64)  NOT NULL,           -- NEW: the store category (e.g., Apparel)
  partNumber    VARCHAR(64)  NOT NULL,           -- camelCase column name
  quantity      INT          NOT NULL DEFAULT 0,
  price         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed a basic ROLE_USER so new registrations can attach this
INSERT INTO roles (name) VALUES ('ROLE_USER');
