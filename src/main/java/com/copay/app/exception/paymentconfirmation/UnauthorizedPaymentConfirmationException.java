package com.copay.app.exception.paymentconfirmation;

import java.io.Serial;

public class UnauthorizedPaymentConfirmationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedPaymentConfirmationException(String message) {
        super(message);
    }
}