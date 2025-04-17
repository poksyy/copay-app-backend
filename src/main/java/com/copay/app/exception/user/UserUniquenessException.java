package com.copay.app.exception.user;

public class UserUniquenessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserUniquenessException(String message) {
		super(message);
	}
}
