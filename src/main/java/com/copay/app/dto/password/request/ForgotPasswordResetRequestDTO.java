package com.copay.app.dto.password.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordResetRequestDTO {

	@NotBlank(message = "Password must not be null")
	private String newPassword;

	@NotBlank(message = "Confirm password must not be null")
	private String confirmNewPassword;

	@AssertTrue(message = "Passwords must match")
	public boolean isPasswordsMatching() {
		return newPassword != null && newPassword.equals(confirmNewPassword);
	}

	// Getters y setters

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

}