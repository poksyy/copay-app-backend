package com.copay.app.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserCreateDTO {

	@NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phoneNumber;
    
    @NotBlank(message = "Password must not be blank")
    private String password;

    public UserCreateDTO() {
    }

    public UserCreateDTO(String username, String email, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
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
