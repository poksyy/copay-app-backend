package com.copay.app.dto.user.response.profile;

import com.copay.app.entity.User;

public class EmailResponseDTO {
    private Long userId;
    private String email;

    public EmailResponseDTO(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}