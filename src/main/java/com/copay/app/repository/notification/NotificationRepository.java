package com.copay.app.repository.notification;

import com.copay.app.entity.Notification;
import com.copay.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific user
     * 
     * @param user the user whose notifications to find
     * @return list of notifications for the user
     */
    List<Notification> findByUser(User user);
    
    /**
     * Find all notifications for a specific user by user ID
     * 
     * @param userId the ID of the user whose notifications to find
     * @return list of notifications for the user
     */
    List<Notification> findByUserUserId(Long userId);
    
    /**
     * Find all notifications for a specific user filtered by read status
     * 
     * @param user the user whose notifications to find
     * @param isRead the read status to filter by
     * @return list of notifications for the user with the specified read status
     */
    List<Notification> findByUserAndIsRead(User user, boolean isRead);
    
    /**
     * Find all notifications for a specific user by user ID filtered by read status
     * 
     * @param userId the ID of the user whose notifications to find
     * @param isRead the read status to filter by
     * @return list of notifications for the user with the specified read status
     */
    List<Notification> findByUserUserIdAndIsRead(Long userId, boolean isRead);
    
    /**
     * Count unread notifications for a specific user
     * 
     * @param user the user whose unread notifications to count
     * @return count of unread notifications
     */
    long countByUserAndIsRead(User user, boolean isRead);
    
    /**
     * Count unread notifications for a specific user by user ID
     * 
     * @param userId the ID of the user whose unread notifications to count
     * @return count of unread notifications
     */
    long countByUserUserIdAndIsRead(Long userId, boolean isRead);
}