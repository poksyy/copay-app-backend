package com.copay.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdatePhoneNumberRequest {

	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Phone number cannot be empty")
	private String phoneNumber;

	// Constructor empty.
	public UpdatePhoneNumberRequest() {
	}

	// Constructor.
	public UpdatePhoneNumberRequest(String email, String phoneNumber) {
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	// Getters and Setters.
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
