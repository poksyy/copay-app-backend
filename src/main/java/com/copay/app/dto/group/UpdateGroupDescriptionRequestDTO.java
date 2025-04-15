package com.copay.app.dto.group;

import jakarta.validation.constraints.Size;

public class UpdateGroupDescriptionRequestDTO {

	@Size(max = 50, message = "Description must be no longer than 50 characters")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
