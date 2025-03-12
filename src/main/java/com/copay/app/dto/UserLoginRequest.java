package com.copay.app.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {

    @NotBlank(message = "Phone number cannot be empty")
    private String phone_number;

    @NotBlank(message = "Password cannot be empty")
    private String password;
   

    // Constructor for deserialization.
    public UserLoginRequest() {}

    public UserLoginRequest(String phone_number, String password) {
        this.phone_number = phone_number;
        this.password = password;
   
    }

    // Getters and Setters.
    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
