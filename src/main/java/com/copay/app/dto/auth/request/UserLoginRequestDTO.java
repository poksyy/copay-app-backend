package com.copay.app.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserLoginRequestDTO {

	@NotBlank(message = "Phone number must not be null")
	@Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
	@Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
	private String phoneNumber;

	@NotBlank(message = "Password must not be null")
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
