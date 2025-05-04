-- Furina database schema.
-- Apply with: mysql -u root -p < src/main/resources/db/schema.sql

CREATE TABLE IF NOT EXISTS food_data
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    thumbnail_path   VARCHAR(255),
    title            VARCHAR(255)  NOT NULL,
    rating           FLOAT,
    sales_monthly    INT,
    average_cost     DECIMAL(10, 2),
    delivery_time    INT,
    featured_comment VARCHAR(512)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS food_data_item
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    food_data_id INT            NOT NULL,
    name         VARCHAR(255)   NOT NULL,
    description  VARCHAR(512),
    price        DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_food_data_item_food
        FOREIGN KEY (food_data_id) REFERENCES food_data (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_food_data_title ON food_data (title);
CREATE INDEX idx_food_data_item_food_data_id ON food_data_item (food_data_id);

CREATE TABLE IF NOT EXISTS account
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(64)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    role       VARCHAR(32)  NOT NULL DEFAULT 'USER',
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
