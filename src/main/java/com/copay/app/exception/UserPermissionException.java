package com.copay.app.exception;

public class UserPermissionException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPermissionException(String message) {
        super(message);
    }
}
