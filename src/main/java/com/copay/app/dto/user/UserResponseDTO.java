package com.copay.app.dto.user;

import com.copay.app.entity.User;

public class UserResponseDTO {

	private Long id;
	private String username;
	private String email;
	private String password;
	private String phone;
	private boolean isCompleted;
	
	// Json Response.
	public UserResponseDTO(User user) {
		this.id = user.getUserId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.phone = user.getPhoneNumber();
		this.password = user.getPassword();
		this.isCompleted = user.isCompleted();
	}

	// Getters and Setters.
	public Long getUserId() {
		return id;
	}

	public void setUserId(Long id) {
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
		return phone;
	}

	public void setPhoneNumber(String phone) {
		this.phone = phone;
	}
	
	public boolean isisCompleted() {
		return isCompleted;
	}

	public void setisCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

}
