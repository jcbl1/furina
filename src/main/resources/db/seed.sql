-- Local development accounts. Both use the password "password".
-- Change or remove these rows before deploying anywhere real.

INSERT INTO account (username, password, role, enabled)
VALUES ('admin', '$2a$10$F8v20RXP2e69pguKlnvBruOk3pXOsbsbxUyYBH7u7RZVkGPQD7mg6', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), enabled = VALUES(enabled);

INSERT INTO account (username, password, role, enabled)
VALUES ('user', '$2a$10$F8v20RXP2e69pguKlnvBruOk3pXOsbsbxUyYBH7u7RZVkGPQD7mg6', 'USER', TRUE)
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), enabled = VALUES(enabled);
