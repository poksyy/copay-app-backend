package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class GetGroupRequestDTO {

    private Long userId;

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
