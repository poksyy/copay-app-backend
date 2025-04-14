package com.copay.app.entity.relations;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.copay.app.entity.Group;

@Entity
@Table(name = "external_members")
public class ExternalMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "external_members_id")
	private Long externalMembersId;

	@Column(nullable = false)
	private String name;

	// LAZY fetch is used to avoid unnecessary loading of external members if not accessed.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	// Empty constructor.
	public ExternalMember() {
		this.joinedAt = LocalDateTime.now();
	}

	// Getters and Setters.
	public Long getExternalMembersId() {
		return externalMembersId;
	}

	public void setExternalMembersId(Long externalMembersId) {
		this.externalMembersId = externalMembersId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
}
