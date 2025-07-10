-- -------------------------------------------
-- Users Table (for login, signup, and roles)
-- -------------------------------------------
USE Stock_Management_System;
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,  -- Use hashed passwords for security
    role ENUM('admin', 'staff') DEFAULT 'staff', -- Using 'admin' and 'staff' roles
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------
-- Categories Table (for product categorization)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- -------------------------------------------
-- Suppliers Table (for supplier info)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255),
    email VARCHAR(255) UNIQUE
);

-- -------------------------------------------
-- Products Table (for product details)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,  -- Foreign Key to Categories
    supplier_id INT NOT NULL,  -- Foreign Key to Suppliers
    price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT DEFAULT 0,  -- Initial stock quantity
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- -------------------------------------------
-- Product Variations Table (for color, size, price)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS product_variations (
    variation_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,  -- Foreign Key to Products
    color VARCHAR(100),
    size VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL,  -- Price for this variation
    stock_quantity INT DEFAULT 0,  -- Quantity available for this variation
    available BOOLEAN DEFAULT TRUE,  -- Whether variation is available or not
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- -------------------------------------------
-- Product Stock Table (for SKU, title, price, stock)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS product_stock (
    sku VARCHAR(255) PRIMARY KEY,  -- Unique SKU for each product variation
    product_id INT NOT NULL,  -- Foreign Key to Products
    product_title VARCHAR(255) NOT NULL,  -- Title of product including variation
    product_price DECIMAL(10, 2) NOT NULL,  -- Price of product variation
    total_available_stock INT DEFAULT 0,  -- Available stock for this SKU
    variation_available BOOLEAN DEFAULT TRUE,  -- Whether variation is available
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- -------------------------------------------
-- Sales Table (for sales transactions)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(255) NOT NULL,  -- Foreign Key to Product Stock (SKU)
    quantity_sold INT NOT NULL,  -- Quantity sold in this transaction
    total_price DECIMAL(10, 2) NOT NULL,  -- Total price for the transaction
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Date and time of the sale
    user_id INT NOT NULL,  -- Foreign Key to Users (who processed the sale)
    FOREIGN KEY (sku) REFERENCES product_stock(sku),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -------------------------------------------
-- Stock Adjustments Table (for stock changes)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS stock_adjustments (
    adjustment_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,  -- Foreign Key to Products
    adjustment_type ENUM('addition', 'removal') NOT NULL,  -- Whether stock is added or removed
    quantity INT NOT NULL,
    reason VARCHAR(255),
    adjustment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,  -- Foreign Key to Users
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -------------------------------------------
-- Profile Table (for user profile management)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS user_profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  -- Foreign Key to Users
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -------------------------------------------
-- Audit Log Table (for tracking changes in the system)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action_type ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    table_name VARCHAR(255) NOT NULL,
    record_id INT NOT NULL,  -- ID of the modified record
    old_value TEXT,  -- Previous value (if applicable)
    new_value TEXT,  -- New value (if applicable)
    changed_by INT NOT NULL,  -- User who made the change
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp of the change
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
);
