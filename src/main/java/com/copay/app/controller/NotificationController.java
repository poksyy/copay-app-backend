package com.copay.app.controller;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.notification.response.NotificationListResponseDTO;
import com.copay.app.dto.notification.response.NotificationResponseDTO;
import com.copay.app.service.JwtService;
import com.copay.app.service.notification.NotificationService;
import com.copay.app.service.query.UserQueryService;
import jakarta.mail.Message;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get all notifications for the logged user
     *
     * @return ResponseEntity containing a list of notifications
     */
    @GetMapping
    public ResponseEntity<NotificationListResponseDTO> getAllNotifications() {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        NotificationListResponseDTO notificationListResponseDTO = notificationService.getAllNotifications(token);

        return ResponseEntity.ok(notificationListResponseDTO);
    }

    /**
     * Get unread notifications for the authenticated user
     *
     * @return ResponseEntity containing a list of unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<NotificationListResponseDTO> getUnreadNotifications() {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        NotificationListResponseDTO notificationListResponseDTO = notificationService.getUnreadNotifications(token);

        return ResponseEntity.ok(notificationListResponseDTO);
    }

    /**
     * Mark a notification as read
     *
     * @param notificationId ID of the notification to mark as read
     * @return ResponseEntity containing the updated notification
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<MessageResponseDTO> markNotificationAsRead(@PathVariable Long notificationId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        MessageResponseDTO messageResponseDTO = notificationService.markNotificationAsRead(notificationId, token);

        return ResponseEntity.ok(messageResponseDTO);
    }

    /**
     * Mark all notifications as read for the authenticated user
     *
     * @return ResponseEntity containing a success message
     */
    @PatchMapping("/read-all")
    public ResponseEntity<MessageResponseDTO> markAllNotificationsAsRead() {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        int count = notificationService.markAllNotificationsAsRead(token);

        return ResponseEntity.ok(new MessageResponseDTO(count + " notifications marked as read"));
    }

    /**
     * Delete a notification
     *
     * @param notificationId ID of the notification to delete
     * @return ResponseEntity containing a success message
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<MessageResponseDTO> deleteNotification(@PathVariable Long notificationId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        MessageResponseDTO messageResponseDTO =  notificationService.deleteNotification(notificationId, token);

        return ResponseEntity.ok(messageResponseDTO);
    }
}
