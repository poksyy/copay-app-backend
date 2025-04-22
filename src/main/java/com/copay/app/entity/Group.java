package com.copay.app.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.GroupMember;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "groups")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long groupId;

	@NotBlank(message = "Group name is required")
	@Size(max = 25, message = "Group name must be no longer than 25 characters")
	@Column(name = "name", nullable = false, length = 25)
	private String name;

	// 'created_by' is a foreign key referencing the 'user_id' in the 'users' table.
	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "currency", nullable = false, length = 10)
	private String currency;

	@Size(max = 50, message = "Description must be no longer than 50 characters")
	@Column(name = "description", length = 50)
	private String description;

	@NotNull(message = "Estimated price is required")
	@DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
	@DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
	@Column(name = "estimated_price", nullable = false)
	private Float estimatedPrice;

	@Column(name = "image_url", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT NULL")
	private String imageUrl;

	@Column(name = "image_provider", length = 50)
	private String imageProvider;

	// CascadeType.ALL propagates all persistence operations to GroupMember.
	// orphanRemoval ensures deletion of GroupMember when removed from the Set.
	// "id.group" is used because GroupMember has a composite key via the
	// @Embeddable GroupMemberId.
	@OneToMany(mappedBy = "id.group", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GroupMember> registeredMembers = new HashSet<>();

	// CascadeType.ALL propagates all persistence operations to ExternalMember.
	// orphanRemoval ensures deletion of ExternalMember when removed from the Set.
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ExternalMember> externalMembers = new HashSet<>();

	// CascadeType.ALL propagates all persistence operations to Expenses.
	// orphanRemoval ensures deletion of ExternalMember when removed from the Set.
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Expense> expenses = new HashSet<>();

	// Getter and Setters.
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getEstimatedPrice() {
		return estimatedPrice;
	}

	public void setEstimatedPrice(Float estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageProvider() {
		return imageProvider;
	}

	public void setImageProvider(String imageProvider) {
		this.imageProvider = imageProvider;
	}

	public Set<GroupMember> getRegisteredMembers() {
		return registeredMembers;
	}

	public void setRegisteredMembers(Set<GroupMember> registeredMembers) {
		this.registeredMembers = registeredMembers;
	}

	public Set<ExternalMember> getExternalMembers() {
		return externalMembers;
	}

	public void setExternalMembers(Set<ExternalMember> externalMembers) {
		this.externalMembers = externalMembers;
	}

	public Set<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(Set<Expense> expenses) {
		this.expenses = expenses;
	}
}
