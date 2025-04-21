package com.copay.app.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequestDTO {

	@NotBlank(message = "Phone number cannot be empty")
	private String phoneNumber;

	@NotBlank(message = "Password cannot be empty")
	private String password;

	// Getters and Setters.
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
