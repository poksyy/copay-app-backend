package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class GetGroupRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    // Getters y Setters.
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
