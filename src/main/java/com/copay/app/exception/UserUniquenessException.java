package com.copay.app.exception;

public class UserUniquenessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserUniquenessException(String message) {
		super(message);
	}
}
