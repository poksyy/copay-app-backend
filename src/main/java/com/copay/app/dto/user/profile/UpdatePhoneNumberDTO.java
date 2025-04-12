package com.copay.app.dto.user.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdatePhoneNumberDTO {
    
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    public UpdatePhoneNumberDTO() {
    }

    public UpdatePhoneNumberDTO(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
