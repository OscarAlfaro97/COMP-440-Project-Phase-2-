CREATE DATABASE IF NOT EXISTS user_auth;
USE user_auth;


CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL
);

-- SELECT * FROM user_auth.users;
-- DELETE FROM user_auth.users;


CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10, 2),
	post_date DATE DEFAULT (CURRENT_DATE) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- SELECT * FROM items;
-- DELETE FROM items;
-- DESCRIBE items


CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    reviewer_username VARCHAR(50),
    rating ENUM('excellent', 'good', 'fair', 'poor'),
    comment TEXT,
    review_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_username) REFERENCES users(username) ON DELETE CASCADE,
    UNIQUE (item_id, reviewer_username)  -- one review per item per user
);
