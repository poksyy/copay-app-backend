package com.copay.app.dto.password;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordResetRequest {

	@NotBlank(message = "New password is required")
	private String newPassword;

	@NotBlank(message = "Confirm password is required")
	private String confirmNewPassword;

	@AssertTrue(message = "Passwords must match")
	public boolean isNewPasswordMatching() {
		return newPassword.equals(confirmNewPassword);
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