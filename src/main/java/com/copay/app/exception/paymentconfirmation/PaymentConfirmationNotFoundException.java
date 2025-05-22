package com.copay.app.exception.paymentconfirmation;

public class PaymentConfirmationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentConfirmationNotFoundException(String message) {
        super(message);
    }
}