package com.copay.app.exception.token;

public class UserIdNotMatchTokenException extends RuntimeException {

    public UserIdNotMatchTokenException(String message) {
        super(message);
    }
}
