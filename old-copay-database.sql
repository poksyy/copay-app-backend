DROP TABLE IF EXISTS `payment_confirmations`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `debts`;
DROP TABLE IF EXISTS `expenses`;
DROP TABLE IF EXISTS `plan_participants`;
DROP TABLE IF EXISTS `plans`;
DROP TABLE IF EXISTS `group_members`;
DROP TABLE IF EXISTS `groups`;
DROP TABLE IF EXISTS `users`;

-- Users Table
CREATE TABLE `users` (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Groups Table
CREATE TABLE `groups` (
    group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Group Members Table
CREATE TABLE `group_members` (
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Plans Table
CREATE TABLE `plans` (
    plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL,
    created_by BIGINT NOT NULL,
    is_confirmed TINYINT DEFAULT 0,
    confirmed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Plan Participants Table
CREATE TABLE `plan_participants` (
    plan_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (plan_id, user_id),
    FOREIGN KEY (plan_id) REFERENCES `plans`(plan_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Expenses Table
CREATE TABLE `expenses` (
    expense_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    added_by BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id) ON DELETE CASCADE,
    FOREIGN KEY (added_by) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Debts Table
CREATE TABLE `debts` (
    debt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    debtor_id BIGINT NOT NULL,
    creditor_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    is_settled TINYINT DEFAULT 0,
    settled_at TIMESTAMP,
    FOREIGN KEY (expense_id) REFERENCES `expenses`(expense_id) ON DELETE CASCADE,
    FOREIGN KEY (debtor_id) REFERENCES `users`(user_id) ON DELETE CASCADE,
    FOREIGN KEY (creditor_id) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Notifications Table
CREATE TABLE `notifications` (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    is_read TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `users`(user_id) ON DELETE CASCADE
);

-- Payment Confirmations Table
CREATE TABLE `payment_confirmations` (
    confirmation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_id BIGINT NOT NULL,
    sms_code VARCHAR(10) NOT NULL,
    is_confirmed TINYINT DEFAULT 0,
    confirmed_at TIMESTAMP NULL,
    FOREIGN KEY (debt_id) REFERENCES `debts`(debt_id) ON DELETE CASCADE
);
