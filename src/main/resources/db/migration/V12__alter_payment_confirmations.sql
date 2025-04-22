-- Alter payment_confirmations table
ALTER TABLE payment_confirmations
    CHANGE COLUMN confirmation_id payment_confirmation_id BIGINT AUTO_INCREMENT;