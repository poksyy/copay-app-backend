package com.copay.app.service.notification;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.notification.response.NotificationListResponseDTO;
import com.copay.app.dto.notification.response.NotificationResponseDTO;
import com.copay.app.entity.Notification;
import com.copay.app.entity.User;
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
    @Transactional
    public Notification createNotification(User user, String message) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(Long userId) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator
        userQueryService.getUserById(userId); // Validate user exists
        return notificationRepository.findByUserUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(User user) {

        return notificationRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator
        userQueryService.getUserById(userId); // Validate user exists
        return notificationRepository.findByUserUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUser(User user) {

        return notificationRepository.findByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public Notification markNotificationAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public int markAllNotificationsAsRead(String token) {

        // Get the phone number from the current token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Find the user through the UserQueryService.
        User user =  userQueryService.getUserByPhone(phoneNumber);

        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsRead(user, false);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return unreadNotifications.size();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator
        userQueryService.getUserById(userId); // Validate user exists
        return notificationRepository.countByUserUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public MessageResponseDTO deleteNotification(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);

        return new MessageResponseDTO("Notification deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationDTOById(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        return new NotificationResponseDTO(notification);
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

        long unreadCount = notificationRepository.countByUserUserIdAndIsRead(user.getUserId(), false);

        return new NotificationListResponseDTO(notificationDTOs, (int) unreadCount);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponseDTO getUnreadNotificationDTOsByUserId(Long userId) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator
        userQueryService.getUserById(userId);

        List<Notification> notifications = notificationRepository.findByUserUserIdAndIsRead(userId, false);
        List<NotificationResponseDTO> notificationDTOs = notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());

        long unreadCount = notifications.size();

        return new NotificationListResponseDTO(notificationDTOs, (int) unreadCount);
    }
}
