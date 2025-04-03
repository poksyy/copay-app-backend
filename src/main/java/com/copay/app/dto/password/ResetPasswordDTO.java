package com.copay.app.dto.password;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordDTO {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
			message = "Password must be at least 8 characters long, contain at least one uppercase letter and one number."
	)
	private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmNewPassword;

	@AssertTrue(message = "Passwords must match")
    public boolean isNewPasswordMatching() {
        return newPassword.equals(confirmNewPassword);
    }
	
    // Getters and Setters.
	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

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
