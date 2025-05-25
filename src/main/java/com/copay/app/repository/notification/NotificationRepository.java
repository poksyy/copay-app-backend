package com.copay.app.repository.notification;

import com.copay.app.entity.Notification;
import com.copay.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * @param read the read status to filter by
     * @return list of notifications for the user with the specified read status
     */
    List<Notification> findByUserAndRead(User user, boolean read);

    /**
     * Custom query to mark all unread notifications as read for a specific user
     *
     * @param user the user whose notifications will be marked as read
     * @return number of notifications updated
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
    int markAllAsReadByUser(@Param("user") User user);

    /**
     * Find all notifications for a specific user by user ID filtered by read status
     * 
     * @param userId the ID of the user whose notifications to find
     * @param read the read status to filter by
     * @return list of notifications for the user with the specified read status
     */
    List<Notification> findByUserUserIdAndRead(Long userId, boolean read);
    
    /**
     * Count unread notifications for a specific user
     * 
     * @param user the user whose unread notifications to count
     * @return count of unread notifications
     */
    long countByUserAndRead(User user, boolean read);
    
    /**
     * Count unread notifications for a specific user by user ID
     * 
     * @param userId the ID of the user whose unread notifications to count
     * @return count of unread notifications
     */
    long countByUserUserIdAndRead(Long userId, boolean read);
}