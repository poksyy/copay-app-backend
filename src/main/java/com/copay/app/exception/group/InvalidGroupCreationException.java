package com.copay.app.exception.group;

import java.io.Serial;

public class InvalidGroupCreationException extends RuntimeException{

	@Serial
	private static final long serialVersionUID = 1L;

	public InvalidGroupCreationException(String message) {
		super(message);
	}
}
