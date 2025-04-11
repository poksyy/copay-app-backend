package com.copay.app.dto.group;

import jakarta.validation.constraints.NotNull;

public class GroupJoinRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
