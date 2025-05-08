package com.copay.app.exception.expense;

public class DebtorNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DebtorNotFoundException(String message) {
        super(message);
    }
}