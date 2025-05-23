package com.copay.app.dto.group.request;

import jakarta.validation.constraints.NotNull;

public class GetGroupsByGroupRequestDTO {

    @NotNull(message = "Group ID must not be null")
    private Long groupId;

    // Getters y Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
}


