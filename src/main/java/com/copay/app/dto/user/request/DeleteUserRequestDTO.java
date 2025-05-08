package com.copay.app.dto.user.request;

import jakarta.validation.constraints.NotNull;

public class DeleteUserRequestDTO {

	@NotNull(message = "Group ID must not be null.")
	private Long userId;

	public DeleteUserRequestDTO(Long userId) {
	        this.userId = userId;
	    }

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}