package com.copay.app.dto.responses;

public class ResetPasswordResponseDTO {

    private String message;

    // Constructor.
    public ResetPasswordResponseDTO(String message) {
        this.message = message;
    }

    // Getters and Setters.
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
