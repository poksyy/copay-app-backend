package com.copay.app.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.copay.app.entity.relations.GroupMember;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "group_name", nullable = false, length = 100)
	private String groupName;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	// OneToMany relation with GroupMember table.
	@OneToMany(mappedBy = "id.group", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GroupMember> groupMembers;

	// Getter and Setters.
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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Set<GroupMember> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}
}