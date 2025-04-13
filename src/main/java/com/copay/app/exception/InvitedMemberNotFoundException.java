package com.copay.app.exception;

public class InvitedMemberNotFoundException extends RuntimeException {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public InvitedMemberNotFoundException(String message) {
        super(message);
    }
}
