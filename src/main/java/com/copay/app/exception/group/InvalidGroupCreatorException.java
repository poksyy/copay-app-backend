package com.copay.app.exception.group;

public class InvalidGroupCreatorException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InvalidGroupCreatorException(String message) {
		super(message);
	}
}
