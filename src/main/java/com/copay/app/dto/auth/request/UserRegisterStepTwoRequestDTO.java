package com.copay.app.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegisterStepTwoRequestDTO {

    @NotBlank(message = "Prefix number must not be null")
    private String phonePrefix;

    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
    @NotBlank(message = "Phone number must not be null")
    private String phoneNumber;

    // Constructor empty.
    public UserRegisterStepTwoRequestDTO() {
    }

    // Constructor.
    public UserRegisterStepTwoRequestDTO(String phoneNumber, String phonePrefix) {
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
