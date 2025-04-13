package com.copay.app.entity.relations;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "group_members")
public class GroupMember {

	// Uses a composite primary key with GroupMemberId to manage the many-to-one
	// relationships for User and Group, avoiding redundant mappings and keeping
	// the User, Group, and GroupMember entities cleaner.
	@EmbeddedId
	private GroupMemberId id;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt = LocalDateTime.now();

	// Default constructor.
	public GroupMember() {
	}

	// Constructor that initializes the group-member relationship with joinedAt.
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
