package com.copay.app.exception.expense;

public class ExpenseNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}