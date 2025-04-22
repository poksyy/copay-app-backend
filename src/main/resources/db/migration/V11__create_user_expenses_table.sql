-- Create user_expenses table with support for users and external_members
CREATE TABLE user_expenses (
    user_expenses_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,

    debtor_user_id BIGINT,
    debtor_external_member_id BIGINT,

    creditor_user_id BIGINT,
    creditor_external_member_id BIGINT,

    amount DECIMAL(10, 2) NOT NULL,

    -- Relationships
    CONSTRAINT fk_user_expenses_expense FOREIGN KEY (expense_id) REFERENCES expenses(expense_id) ON DELETE CASCADE,

    CONSTRAINT fk_user_expenses_debtor_user FOREIGN KEY (debtor_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_expenses_debtor_ext FOREIGN KEY (debtor_external_member_id) REFERENCES external_members(external_members_id) ON DELETE CASCADE,

    CONSTRAINT fk_user_expenses_creditor_user FOREIGN KEY (creditor_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_expenses_creditor_ext FOREIGN KEY (creditor_external_member_id) REFERENCES external_members(external_members_id) ON DELETE CASCADE
);
