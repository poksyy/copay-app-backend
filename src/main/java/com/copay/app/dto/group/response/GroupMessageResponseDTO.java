package com.copay.app.dto.group.response;

public class GroupMessageResponseDTO {

	private String message;
				
	public GroupMessageResponseDTO(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
