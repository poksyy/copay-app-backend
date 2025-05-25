package com.copay.app.service.notification;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.notification.response.NotificationListResponseDTO;
import com.copay.app.dto.notification.response.NotificationResponseDTO;
import com.copay.app.entity.Notification;
import com.copay.app.entity.User;
import com.copay.app.exception.notification.NotificationAccessDeniedException;
import com.copay.app.exception.notification.NotificationNotFoundException;
import com.copay.app.repository.notification.NotificationRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserQueryService userQueryService;

    private final JwtService jwtService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserQueryService userQueryService, JwtService jwtService) {
        this.notificationRepository = notificationRepository;
        this.userQueryService = userQueryService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponseDTO getAllNotifications(String token) {

        // Get the phone number from the current token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Find the user through the UserQueryService.
        User user =  userQueryService.getUserByPhone(phoneNumber);

        // Find notifications of the logged user.
        List<Notification> notifications = notificationRepository.findByUserUserId(user.getUserId());

        // Transform the notifications of the user to DTO.
        List<NotificationResponseDTO> notificationDTOs = notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());

        long unreadCount = notificationRepository.countByUserUserIdAndRead(user.getUserId(), false);

        return new NotificationListResponseDTO(notificationDTOs, (int) unreadCount);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponseDTO getUnreadNotifications(String token) {

        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Find a user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserByPhone(phoneNumber);

        // Query to filter all the unread notifications of the user.
        List<Notification> notifications = notificationRepository.findByUserUserIdAndRead(user.getUserId(), false);

        // Map the notifications to DTO response and count the unread notifications.
        List<NotificationResponseDTO> notificationDTOs = notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());

        long unreadCount = notifications.size();

        return new NotificationListResponseDTO(notificationDTOs, (int) unreadCount);
    }
    
    @Override
    @Transactional
    public MessageResponseDTO markNotificationAsRead(Long notificationId, String token) {

        // Validate the notification ownership before marking it as read.
        Notification notification = validateNotificationOwnership(notificationId, token);

        // Mark the notification as read.
        notification.setRead(true);

        return new MessageResponseDTO("Notification marked as read");
    }

    @Override
    @Transactional
    public int markAllNotificationsAsRead(String token) {

        // Get the phone number from the current token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Find the user through the UserQueryService.
        User user =  userQueryService.getUserByPhone(phoneNumber);

        // Use a custom query to mark all unread notifications as read for the given user.
        return notificationRepository.markAllAsReadByUser(user);
    }

    @Override
    @Transactional
    public void createNotification(User user, String message) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Persist the notification in the database.
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public MessageResponseDTO deleteNotification(Long notificationId, String token) {

        // Validate the notification ownership before deleting it.
        Notification notification = validateNotificationOwnership(notificationId, token);

        notificationRepository.delete(notification);

        return new MessageResponseDTO("Notification deleted successfully");
    }

    // Helper method.
    private Notification validateNotificationOwnership(Long notificationId, String token) {

        // Get the phone number from the current token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Find the user through the UserQueryService.
        User user = userQueryService.getUserByPhone(phoneNumber);

        // Check if the notification exists in the database.
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        // Check ownership and throws exception if the user does not own the notification.
        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new NotificationAccessDeniedException("Access denied: user " + user.getUserId() +
                    " tried to access notification " + notificationId + " without ownership.");
        }

        return notification;
    }
}
