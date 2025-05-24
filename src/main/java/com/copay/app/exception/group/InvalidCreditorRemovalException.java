package com.copay.app.exception.group;

import java.io.Serial;

public class InvalidCreditorRemovalException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCreditorRemovalException(String message) {
        super(message);
    }
}
