package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class DeleteGroupRequestDTO {

    @NotNull(message = "Group ID must not be null.")
    private Long groupId;

    // Getters and setters
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
