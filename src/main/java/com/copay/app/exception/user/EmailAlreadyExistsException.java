package com.copay.app.exception.user;

public class EmailAlreadyExistsException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsException(String message) {
		super(message);
	}

}