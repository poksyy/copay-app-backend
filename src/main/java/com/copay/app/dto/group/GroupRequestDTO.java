package com.copay.app.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GroupRequestDTO {

    @NotBlank(message = "Group name is required")
    private String groupName;

    @NotNull(message = "Creator user id is required")
    private Long createdBy;

    // Getters and Setters
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public Long getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
