package com.copay.app.dto.user.request.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateEmailRequestDTO {
    
    @NotBlank(message = "Email must not be null")
    @Email(message = "Invalid email format")
    private String email;

    public UpdateEmailRequestDTO() {
    }

    public UpdateEmailRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}