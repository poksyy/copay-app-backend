package com.copay.app.exception.user;

import java.io.Serial;

public class UserPermissionException extends RuntimeException {
    /**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public UserPermissionException(String message) {
        super(message);
    }
}
