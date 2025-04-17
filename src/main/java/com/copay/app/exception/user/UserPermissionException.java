package com.copay.app.exception.user;

public class UserPermissionException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPermissionException(String message) {
        super(message);
    }
}
