package com.copay.app.entity.relations;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "group_members")
public class GroupMember {

    @EmbeddedId
    private GroupMemberId id;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    // Default constructor.
    public GroupMember() {}

    // Constructor.
    public GroupMember(GroupMemberId id) {
        this.id = id;
        this.joinedAt = LocalDateTime.now();
    }

    // Getters and Setters.
    public GroupMemberId getId() {
        return id;
    }

    public void setId(GroupMemberId id) {
        this.id = id;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
