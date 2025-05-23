package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class GetGroupRequestDTO {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Group ID must not be null")
    private Long groupId;

    // Getters and Setters.
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
