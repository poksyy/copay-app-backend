package com.copay.app.dto.user.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdatePhoneNumberRequestDTO {

    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
    @NotBlank(message = "Phone number must not be null")
    private String phoneNumber;

    public UpdatePhoneNumberRequestDTO() {
    }

    public UpdatePhoneNumberRequestDTO(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
