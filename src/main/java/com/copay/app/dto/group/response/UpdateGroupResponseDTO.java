package com.copay.app.dto.group.response;

public class UpdateGroupResponseDTO {

	private String updateMessage;
				
	public UpdateGroupResponseDTO(String updateMessage) {
		this.updateMessage = updateMessage;
	}

	public String getMessage() {
		return updateMessage;
	}

	public void setMessage(String updateMessage) {
		this.updateMessage = updateMessage;
	}
}
