package com.copay.app.dto.user.profile;

import jakarta.validation.constraints.NotBlank;

public class UpdateUsernameDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    public UpdateUsernameDTO() {}

    public UpdateUsernameDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
