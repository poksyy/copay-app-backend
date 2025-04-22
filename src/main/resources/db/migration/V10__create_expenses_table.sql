-- Create expenses table
CREATE TABLE `expenses` (
    expense_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,

    title VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_by_user_id BIGINT,
    paid_by_external_member_id BIGINT,
    created_at DATETIME NOT NULL,

    -- Relationships
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id),
    FOREIGN KEY (paid_by_user_id) REFERENCES `users`(user_id),
    FOREIGN KEY (paid_by_external_member_id) REFERENCES `external_members`(external_members_id)
);