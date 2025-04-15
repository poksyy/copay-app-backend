package com.copay.app.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateGroupNameRequestDTO {

	@NotBlank(message = "Group name is required")
	@Size(max = 25, message = "Group name must be no longer than 25 characters")    
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
