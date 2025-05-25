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
    private final JwtService jwtService;
    private final UserQueryService userQueryService;

    public NotificationController(NotificationService notificationService, JwtService jwtService, UserQueryService userQueryService) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
        this.userQueryService = userQueryService;
    }

    /**
     * Get all notifications for the authenticated user
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

        String phoneNumber = jwtService.getUserIdentifierFromToken(token);
        Long userId = userQueryService.getUserByPhone(phoneNumber).getUserId();

        NotificationListResponseDTO notifications = notificationService.getUnreadNotificationDTOsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get a specific notification by ID
     *
     * @param notificationId ID of the notification to retrieve
     * @return ResponseEntity containing the notification
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long notificationId) {

        NotificationResponseDTO notification = notificationService.getNotificationDTOById(notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * Mark a notification as read
     *
     * @param notificationId ID of the notification to mark as read
     * @return ResponseEntity containing the updated notification
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO> markNotificationAsRead(@PathVariable Long notificationId) {

        notificationService.markNotificationAsRead(notificationId);

        NotificationResponseDTO notification = notificationService.getNotificationDTOById(notificationId);

        return ResponseEntity.ok(notification);
    }

    /**
     * Mark all notifications as read for the authenticated user
     *
     * @return ResponseEntity containing a success message
     */
    @PutMapping("/read-all")
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

        MessageResponseDTO messageResponseDTO =  notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok(messageResponseDTO);
    }
}
