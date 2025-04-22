--  Drop old tables if they exist
ALTER TABLE payment_confirmations DROP FOREIGN KEY payment_confirmations_ibfk_1;

DROP TABLE IF EXISTS debts;
DROP TABLE IF EXISTS expenses;