ALTER TABLE `groups` DROP COLUMN name;
ALTER TABLE `groups` CHANGE group_name name VARCHAR(255);
    