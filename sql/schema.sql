CREATE DATABASE IF NOT EXISTS school_suggestion_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE school_suggestion_db;

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS suggestions (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    date_submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_suggestions_student
        FOREIGN KEY (student_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- Seed credentials: username=admin, password=admin123.
-- The password is stored as PBKDF2WithHmacSHA256 (120,000 iterations), not plaintext.
INSERT INTO users (full_name, username, `password`, role)
VALUES (
    'School Administrator',
    'admin',
    'pbkdf2$120000$AQIDBAUGBwgJCgsMDQ4PEA==$uIWw9x+WoLETdS8akh9PW+CZW55BzJmGY4H8jjZFt+o=',
    'ADMIN'
)
ON DUPLICATE KEY UPDATE username = VALUES(username);
