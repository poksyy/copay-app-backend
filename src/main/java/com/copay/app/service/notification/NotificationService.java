package com.copay.app.service.notification;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.notification.response.NotificationListResponseDTO;
import com.copay.app.dto.notification.response.NotificationResponseDTO;
import com.copay.app.entity.Notification;
import com.copay.app.entity.User;

import java.util.List;

/**
 * Service interface for managing notifications
 */
public interface NotificationService {


    /**
     * Create a new notification for a user
     *
     * @param user the user to create the notification for
     * @param message the notification message
     * @return the created notification
     */
    Notification createNotification(User user, String message);

    /**
     * Get all notifications for a user
     *
     * @param userId the ID of the user to get notifications for
     * @return list of notifications for the user
     */
    List<Notification> getNotificationsByUserId(Long userId);

    /**
     * Get all notifications for a user
     *
     * @param user the user to get notifications for
     * @return list of notifications for the user
     */
    List<Notification> getNotificationsByUser(User user);

    /**
     * Get unread notifications for a user
     *
     * @param userId the ID of the user to get unread notifications for
     * @return list of unread notifications for the user
     */
    List<Notification> getUnreadNotificationsByUserId(Long userId);

    /**
     * Get unread notifications for a user
     *
     * @param user the user to get unread notifications for
     * @return list of unread notifications for the user
     */
    List<Notification> getUnreadNotificationsByUser(User user);

    /**
     * Mark a notification as read
     *
     * @param notificationId the ID of the notification to mark as read
     * @return the updated notification
     */
    Notification markNotificationAsRead(Long notificationId);

    /**
     * Mark all notifications for a user as read
     *
     * @param token to identify the ID of the user and mark notifications as read for
     * @return the number of notifications marked as read
     */
    int markAllNotificationsAsRead(String token);

    /**
     * Count unread notifications for a user
     *
     * @param userId the ID of the user to count unread notifications for
     * @return the count of unread notifications
     */
    long countUnreadNotifications(Long userId);

    /**
     * Delete a notification
     *
     * @param notificationId the ID of the notification to delete
     */
    MessageResponseDTO deleteNotification(Long notificationId);

    /**
     * Get a notification by ID as DTO
     *
     * @param notificationId the ID of the notification to get
     * @return the notification as DTO
     */
    NotificationResponseDTO getNotificationDTOById(Long notificationId);

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
     * @param userId the ID of the user to get unread notifications for
     * @return list of unread notifications for the user as DTOs
     */
    NotificationListResponseDTO getUnreadNotificationDTOsByUserId(Long userId);
}
