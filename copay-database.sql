CREATE DATABASE copay;
USE copay;

-- Users Table: Stores user information
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Groups Table: Stores information about expense groups
CREATE TABLE `Groups` (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
);

-- Group Members Table: Records which users belong to which groups
CREATE TABLE Group_Members (
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES `Groups`(group_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Expenses Table: Stores information about expenses added to groups
CREATE TABLE Expenses (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    added_by INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,  -- Added field for currency
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `Groups`(group_id),
    FOREIGN KEY (added_by) REFERENCES Users(user_id)
);

-- Debts Table: Records debts between users within a group
CREATE TABLE Debts (
    debt_id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    debtor_id INT NOT NULL,
    creditor_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,  -- Added field for currency
    is_settled BOOLEAN DEFAULT FALSE,
    settled_at TIMESTAMP,
    FOREIGN KEY (expense_id) REFERENCES Expenses(expense_id),
    FOREIGN KEY (debtor_id) REFERENCES Users(user_id),
    FOREIGN KEY (creditor_id) REFERENCES Users(user_id)
);

-- Wishlists Table: Stores shared wishlists for groups
CREATE TABLE Wishlists (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    created_by INT NOT NULL,
    wishlist_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `Groups`(group_id),
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
);

-- Products Table: Stores products added to wishlists
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    wishlist_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    added_by INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wishlist_id) REFERENCES Wishlists(wishlist_id),
    FOREIGN KEY (added_by) REFERENCES Users(user_id)
);

-- Price Comparisons Table: Stores price comparison data for products
CREATE TABLE Price_Comparisons (
    comparison_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    store_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,  -- Added field for currency
    comparison_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Notifications Table: Stores payment and debt notifications for users
CREATE TABLE Notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Payment Confirmations Table: Records payment confirmations via SMS
CREATE TABLE Payment_Confirmations (
    confirmation_id INT AUTO_INCREMENT PRIMARY KEY,
    debt_id INT NOT NULL,
    sms_code VARCHAR(10) NOT NULL,
    is_confirmed BOOLEAN DEFAULT FALSE,
    confirmed_at TIMESTAMP,
    FOREIGN KEY (debt_id) REFERENCES Debts(debt_id)
);