package com.copay.app.dto.paymentconfirmation.response;

public class ListUnconfirmedPaymentConfirmationResponseDTO {
    private final Long userExpenseId;
    private final Float confirmationAmount;

    public ListUnconfirmedPaymentConfirmationResponseDTO(Long userExpenseId, Float confirmationAmount) {
        this.userExpenseId = userExpenseId;
        this.confirmationAmount = confirmationAmount;
    }

    // Getters and Setters.
    public Long getUserExpenseId() {
        return userExpenseId;
    }

    public Float getConfirmationAmount() {
        return confirmationAmount;
    }
}
