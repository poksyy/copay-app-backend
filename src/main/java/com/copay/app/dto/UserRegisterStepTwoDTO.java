package com.copay.app.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRegisterStepTwoDTO {

	@NotBlank(message = "Phone number cannot be empty")
	private String phoneNumber;

	// Constructor empty.
	public UserRegisterStepTwoDTO() {
	}

	// Constructor.
	public UserRegisterStepTwoDTO(String phoneNumber) {
	
		this.phoneNumber = phoneNumber;
	}

	// Getters and Setters.
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
