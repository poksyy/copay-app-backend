package com.copay.app.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public class UserRegisterStepTwoDTO {

    @NotBlank(message = "Prefix number cannot be empty")
    private String phonePrefix;
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    // Constructor empty.
    public UserRegisterStepTwoDTO() {
    }

    // Constructor.
    public UserRegisterStepTwoDTO(String phoneNumber, String phonePrefix) {
        this.phoneNumber = phoneNumber;
        this.phonePrefix = phonePrefix;
    }

    // Getters and Setters.
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }
}
