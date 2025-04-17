package com.copay.app.dto.auth.request;

import jakarta.validation.constraints.*;

public class UserRegisterStepOneDTO {

	@NotBlank(message = "Username cannot be empty")
	@Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
	private String username;

	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
	@Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
	private String password;

	@NotBlank(message = "Confirm password cannot be empty")
	private String confirmPassword;

	@AssertTrue(message = "Passwords must match")
	public boolean isPasswordsMatching() {
		return password != null && password.equals(confirmPassword);
	}

	// Constructor for deserialization.
	public UserRegisterStepOneDTO() {
	}

	// Constructor with parameters.
	public UserRegisterStepOneDTO(String username, String email, String password, String confirmPassword) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	// Getters and Setters.
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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
