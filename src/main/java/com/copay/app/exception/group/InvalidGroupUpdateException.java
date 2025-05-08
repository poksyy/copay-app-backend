package com.copay.app.exception.group;

public class InvalidGroupUpdateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidGroupUpdateException(String message) {
		super(message);
	}
}
