package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class GetGroupsByUserRequestDTO {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    // Getters y Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
