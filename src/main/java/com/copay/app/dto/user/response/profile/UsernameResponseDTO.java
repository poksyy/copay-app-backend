package com.copay.app.dto.user.response.profile;

import com.copay.app.entity.User;

public class UsernameResponseDTO {

    private Long userId;
    private String username;

    public UsernameResponseDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
    }

    // Getters and Setters.
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}