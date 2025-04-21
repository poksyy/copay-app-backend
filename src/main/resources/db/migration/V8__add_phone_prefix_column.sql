-- Add the 'phone_prefix' column to the 'users' table
ALTER TABLE users MODIFY COLUMN phone_prefix VARCHAR(5) NULL;