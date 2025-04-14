package com.copay.app.dto.responses;

public class DeleteGroupResponseDTO {
	
    private String successMessage;

    // Constructor.
    public DeleteGroupResponseDTO(String successMessage) {
        this.successMessage = successMessage;
    }

    // Getters and Setters
    public String getMessage() {
        return successMessage;
    }

    public void setMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
