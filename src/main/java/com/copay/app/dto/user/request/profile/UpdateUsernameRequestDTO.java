package com.copay.app.dto.user.request.profile;

import jakarta.validation.constraints.NotBlank;

public class UpdateUsernameRequestDTO {
    @NotBlank(message = "Username must not be null")
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
