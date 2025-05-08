package com.copay.app.dto.user.request;

import jakarta.validation.constraints.NotNull;

public class GetUserByIdRequestDTO {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    // Getters and Setters.
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
