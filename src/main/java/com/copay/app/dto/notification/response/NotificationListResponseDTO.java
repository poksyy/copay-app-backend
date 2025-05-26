package com.copay.app.dto.notification.response;

import java.util.List;

public class NotificationListResponseDTO {
    
    private List<NotificationResponseDTO> notifications;
    private int totalCount;
    private int unreadCount;
    
    public NotificationListResponseDTO() {
    }
    
    public NotificationListResponseDTO(List<NotificationResponseDTO> notifications, int unreadCount) {
        this.notifications = notifications;
        this.totalCount = notifications.size();
        this.unreadCount = unreadCount;
    }
    
    // Getters and Setters.
    public List<NotificationResponseDTO> getNotifications() {
        return notifications;
    }
    
    public void setNotifications(List<NotificationResponseDTO> notifications) {
        this.notifications = notifications;
        this.totalCount = notifications.size();
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}