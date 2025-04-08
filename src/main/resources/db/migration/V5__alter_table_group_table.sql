-- Add currency field to groups table
ALTER TABLE `groups`
ADD COLUMN currency VARCHAR(10) NOT NULL;

-- Add description field to groups table
ALTER TABLE `groups`
ADD COLUMN description VARCHAR(50);

-- Add estimated_price field to groups table
ALTER TABLE `groups`
ADD COLUMN estimated_price FLOAT NOT NULL CHECK (estimated_price >= 0 AND estimated_price <= 10000000);

-- Add image_url field to groups table
ALTER TABLE `groups`
ADD COLUMN image_url VARCHAR(255) DEFAULT NULL;

-- Add image_provider field to groups table
ALTER TABLE `groups`
ADD COLUMN image_provider VARCHAR(50) DEFAULT NULL;

-- Modify group_name column to have a maximum length of 25 characters
ALTER TABLE `groups`
MODIFY COLUMN group_name VARCHAR(25) NOT NULL;
