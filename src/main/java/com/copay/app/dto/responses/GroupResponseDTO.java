package com.copay.app.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

public class GroupResponseDTO {

    private Long groupId;
    private String groupName;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<Long> memberIds; 

    // Getters and Setters
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<Long> getMemberIds() {
        return memberIds;
    }
    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
