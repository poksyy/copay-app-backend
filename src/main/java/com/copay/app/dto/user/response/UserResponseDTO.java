package com.copay.app.dto.user.response;

import com.copay.app.entity.User;

public class UserResponseDTO {

	private Long id;
	private String username;
	private String email;
	private String password;
	private String phoneNumber;
	private boolean isProfileCompleted;

	// Constructor
	public UserResponseDTO(User user) {
		this.id = user.getUserId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.password = user.getPassword();
		this.isProfileCompleted = user.isCompleted();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isProfileCompleted() {
		return isProfileCompleted;
	}

	public void setProfileCompleted(boolean profileCompleted) {
		isProfileCompleted = profileCompleted;
	}
}
