package com.copay.app.dto.user.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUsernameRequestDTO {

    @NotBlank(message = "Username must not be null")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    private String username;

    public UpdateUsernameRequestDTO() {}

    public UpdateUsernameRequestDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
