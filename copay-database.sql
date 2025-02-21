-- Users Table
CREATE TABLE `users` (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Groups Table (con comillas invertidas)
CREATE TABLE `groups` (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES `users`(user_id)
);

-- Group Members Table
CREATE TABLE `group_members` (
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id),
    FOREIGN KEY (user_id) REFERENCES `users`(user_id)
);

-- Plans Table
CREATE TABLE `plans` (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL,
    created_by INT NOT NULL,
    is_confirmed TINYINT DEFAULT 0,
    confirmed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES `users`(user_id)
);

-- Plan Participants Table
CREATE TABLE `plan_participants` (
    plan_id INT NOT NULL,
    user_id INT NOT NULL,
    share DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (plan_id, user_id),
    FOREIGN KEY (plan_id) REFERENCES `plans`(plan_id),
    FOREIGN KEY (user_id) REFERENCES `users`(user_id)
);

-- Expenses Table
CREATE TABLE `expenses` (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    added_by INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id),
    FOREIGN KEY (added_by) REFERENCES `users`(user_id)
);

-- Debts Table
CREATE TABLE `debts` (
    debt_id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    debtor_id INT NOT NULL,
    creditor_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    is_settled TINYINT DEFAULT 0,
    settled_at TIMESTAMP,
    FOREIGN KEY (expense_id) REFERENCES `expenses`(expense_id),
    FOREIGN KEY (debtor_id) REFERENCES `users`(user_id),
    FOREIGN KEY (creditor_id) REFERENCES `users`(user_id)
);

-- Notifications Table
CREATE TABLE `notifications` (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `users`(user_id)
);

-- Payment Confirmations Table
CREATE TABLE `payment_confirmations` (
    confirmation_id INT AUTO_INCREMENT PRIMARY KEY,
    debt_id INT NOT NULL,
    sms_code VARCHAR(10) NOT NULL,
    is_confirmed TINYINT DEFAULT 0,
    confirmed_at TIMESTAMP NULL,
    FOREIGN KEY (debt_id) REFERENCES `debts`(debt_id)
);
