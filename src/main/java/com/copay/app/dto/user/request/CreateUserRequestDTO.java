package com.copay.app.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserRequestDTO {

	@NotBlank(message = "Username must not be null")
    private String username;
    
    @NotBlank(message = "Email must not be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Prefix number must not be null")
    private String phonePrefix;

    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
    private String phoneNumber;
    
    @NotBlank(message = "Password must not be null")
    private String password;

    public CreateUserRequestDTO() {
    }

    public CreateUserRequestDTO(String username, String email, String phonePrefix, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
        this.phonePrefix = phonePrefix;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
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
