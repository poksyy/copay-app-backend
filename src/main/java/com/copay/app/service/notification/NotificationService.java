package com.copay.app.service.notification;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.notification.response.NotificationListResponseDTO;
import com.copay.app.dto.notification.response.NotificationResponseDTO;
import com.copay.app.entity.User;

/**
 * Service interface for managing notifications
 */
public interface NotificationService {

    /**
     * Get all notifications for a user as DTOs
     *
     * @param token to identify the user and get notifications for
     * @return list of notifications for the user as DTOs
     */
    NotificationListResponseDTO getAllNotifications(String token);

    /**
     * Get unread notifications for a user as DTOs
     *
     * @param token to identify the ID of the user and get all unread notifications for
     * @return list of unread notifications for the user as DTOs
     */
    NotificationListResponseDTO getUnreadNotifications(String token);

    /**
     * Mark a notification as read
     *
     * @param notificationId the ID of the notification to mark as read
     * @return the updated notification
     */
    MessageResponseDTO markNotificationAsRead(Long notificationId, String token);

    /**
     * Mark all notifications for a user as read
     *
     * @param token to identify the ID of the user and mark notifications as read for
     * @return the number of notifications marked as read
     */
    int markAllNotificationsAsRead(String token);

    /**
     * Create a new notification for a user
     *
     * @param user the user to create the notification for
     * @param message the notification message
     */
    void createNotification(User user, String message);


    /**
     * Delete a notification
     *
     * @param notificationId the ID of the notification to delete
     */
    MessageResponseDTO deleteNotification(Long notificationId, String token);
}
