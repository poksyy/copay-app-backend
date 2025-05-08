package com.copay.app.dto.user.request.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateEmailDTO {
    
    @NotBlank(message = "Email must not be null")
    @Email(message = "Invalid email format")
    private String email;

    public UpdateEmailDTO() {
    }

    public UpdateEmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}