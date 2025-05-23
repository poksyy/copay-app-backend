package com.copay.app.dto.paymentconfirmation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ConfirmPaymentRequestDTO {

    @NotNull(message = "User Expense ID is required")
    private Long userExpenseId;

    @NotNull(message = "Confirmation amount is required")
    @Positive(message = "Confirmation amount must be positive")
    private Float confirmationAmount;

    public ConfirmPaymentRequestDTO() {
    }

    public ConfirmPaymentRequestDTO(Long userExpenseId, Float confirmationAmount) {
        this.userExpenseId = userExpenseId;
        this.confirmationAmount = confirmationAmount;
    }

    // Getters and Setters.
    public Long getUserExpenseId() {
        return userExpenseId;
    }

    public void setUserExpenseId(Long userExpenseId) {
        this.userExpenseId = userExpenseId;
    }

    public Float getConfirmationAmount() {
        return confirmationAmount;
    }

    public void setConfirmationAmount(Float confirmationAmount) {
        this.confirmationAmount = confirmationAmount;
    }
}
