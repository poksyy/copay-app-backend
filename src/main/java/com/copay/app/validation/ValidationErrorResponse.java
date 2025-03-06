package com.copay.app.validation;

import java.util.List;

public class ValidationErrorResponse {
    private List<String> errors;
    private String message;
    private int status;

    // Constructor.
    public ValidationErrorResponse(List<String> errors, String message, int status) {
        this.errors = errors;
        this.message = message;
        this.status = status;
    }

    // Getters and Setters.
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
