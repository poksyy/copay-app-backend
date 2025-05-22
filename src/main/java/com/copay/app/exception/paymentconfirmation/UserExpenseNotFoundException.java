package com.copay.app.exception.paymentconfirmation;

import java.io.Serial;

public class UserExpenseNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserExpenseNotFoundException(String message) {
        super(message);
    }
}
