-- Alter total_amount column in expenses from DECIMAL to FLOAT
ALTER TABLE expenses MODIFY total_amount FLOAT;

-- Alter amount column in user_expenses from DECIMAL to FLOAT
ALTER TABLE user_expenses MODIFY amount FLOAT;

-- Alter confirmation_amount column in payment_confirmations from DECIMAL to FLOAT
ALTER TABLE payment_confirmations MODIFY confirmation_amount FLOAT;
