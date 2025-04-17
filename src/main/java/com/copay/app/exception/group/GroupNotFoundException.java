package com.copay.app.exception.group;

public class GroupNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GroupNotFoundException(String message) {
		super(message);
	}
}
