-- Create schema objects in the app database
USE app_db;

CREATE TABLE IF NOT EXISTS items (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO items (name) VALUES ('First item'), ('Second item')
    ON DUPLICATE KEY UPDATE name = VALUES(name);
