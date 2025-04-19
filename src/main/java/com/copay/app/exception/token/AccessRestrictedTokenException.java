package com.copay.app.exception.token;

public class AccessRestrictedTokenException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    public AccessRestrictedTokenException(String message) {
        super(message);
    }
}
