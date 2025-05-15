package com.copay.app.exception.group;

public class RegisteredMemberAlreadyExistsException extends RuntimeException {
    public RegisteredMemberAlreadyExistsException(String message) {
        super(message);
    }
}
