package com.copay.app.exception.group;

public class ExternalMemberNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExternalMemberNotFoundException(String message) {
		super(message);
	}
}
