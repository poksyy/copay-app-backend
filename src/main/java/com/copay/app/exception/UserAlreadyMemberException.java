package com.copay.app.exception;

public class UserAlreadyMemberException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserAlreadyMemberException(String message) {
        super(message);
    }
}
