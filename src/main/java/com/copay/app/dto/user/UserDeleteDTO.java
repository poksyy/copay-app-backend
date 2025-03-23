package com.copay.app.dto.user;

public class UserDeleteDTO {

	private String message;

	public UserDeleteDTO(String message) {
	        this.message = message;
	    }

	public String getMessage() {
		return message;
	}
}