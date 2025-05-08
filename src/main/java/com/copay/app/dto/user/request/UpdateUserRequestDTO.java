package com.copay.app.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDTO {
	
	@NotBlank(message = "Username must not be null")
    private String username;
    
    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
    @NotBlank(message = "Phone number cannot be null")
    private String phoneNumber;
    
    @NotBlank(message = "Password must not be null")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter and one number."
    )
    private String password;

    public UpdateUserRequestDTO() {
    }

    public UpdateUserRequestDTO(String username, String email, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
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
