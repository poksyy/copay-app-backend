package com.copay.app.exception.group;

import java.io.Serial;

public class InvalidGroupUpdateException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public InvalidGroupUpdateException(String message) {
		super(message);
	}
}
