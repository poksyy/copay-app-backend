package com.copay.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.copay.app.entity.relations.GroupMember;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(unique = true, nullable = true)
	private String phoneNumber;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "is_completed", columnDefinition = "TINYINT(1)")
	private boolean isCompleted = false;

    // OneToMany Relation with GroupMember table.
    @OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL, orphanRemoval = true)
    
    private Set<GroupMember> groupMembers;
	
	// Constructor for fake data.
    public User(String username, String email, String password, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.isCompleted = true;
    }
	
    // Empty constructor.
	public User() {
		
	}

	// Getters and Setters.
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean completed) {
		isCompleted = completed;
	}
	
    public Set<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Set<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }
}