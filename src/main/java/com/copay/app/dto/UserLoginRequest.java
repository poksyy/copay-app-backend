package com.copay.app.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;
   

    // Constructor for deserialization.
    public UserLoginRequest() {}

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
   
    }

    // Getters and Setters.
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
