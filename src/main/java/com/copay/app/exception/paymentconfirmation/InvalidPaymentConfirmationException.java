package com.copay.app.exception.paymentconfirmation;

public class InvalidPaymentConfirmationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidPaymentConfirmationException(String message) {
        super(message);
    }
}