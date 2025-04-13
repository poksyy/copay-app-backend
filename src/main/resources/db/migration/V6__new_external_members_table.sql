CREATE TABLE external_members (
    external_members_id BIGINT AUTO_INCREMENT PRIMARY KEY,  
    name VARCHAR(20) NOT NULL,                          
    group_id BIGINT NOT NULL,                            
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `groups`(group_id) ON DELETE CASCADE
);
