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

CREATE TABLE IF NOT EXISTS customer_order
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no         VARCHAR(40)    NOT NULL UNIQUE,
    account_id       BIGINT,
    immediate        BOOLEAN        NOT NULL,
    minutes_from_now INT,
    total_amount     DECIMAL(10, 2) NOT NULL,
    status           VARCHAR(20)    NOT NULL,
    created_at       DATETIME       NOT NULL,
    CONSTRAINT fk_customer_order_account
        FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS customer_order_item
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id          BIGINT         NOT NULL,
    food_data_id      INT            NOT NULL,
    food_data_item_id INT            NOT NULL,
    item_name         VARCHAR(255)   NOT NULL,
    unit_price        DECIMAL(10, 2) NOT NULL,
    quantity          INT            NOT NULL,
    note              VARCHAR(255),
    CONSTRAINT fk_customer_order_item_order
        FOREIGN KEY (order_id) REFERENCES customer_order (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_customer_order_account_id ON customer_order (account_id);
CREATE INDEX idx_customer_order_order_no ON customer_order (order_no);
CREATE INDEX idx_customer_order_item_order_id ON customer_order_item (order_id);
