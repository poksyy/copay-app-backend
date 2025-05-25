package com.copay.app.dto.notification.response;

import com.copay.app.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponseDTO {
    
    private Long notificationId;
    private Long userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    
    public NotificationResponseDTO() {
    }
    
    public NotificationResponseDTO(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.userId = notification.getUser().getUserId();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
    
    // Getters and Setters.
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}