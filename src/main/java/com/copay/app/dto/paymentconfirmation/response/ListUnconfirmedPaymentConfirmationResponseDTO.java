package com.copay.app.dto.paymentconfirmation.response;

public class ListUnconfirmedPaymentConfirmationResponseDTO {

    private Long paymentConfirmationId;
    
    private final Long userExpenseId;

    private final Float confirmationAmount;
    
    private final String username;

    public ListUnconfirmedPaymentConfirmationResponseDTO(Long paymentConfirmationId, Long userExpenseId, Float confirmationAmount, String username) {

        this.paymentConfirmationId = paymentConfirmationId;
        this.userExpenseId = userExpenseId;
        this.confirmationAmount = confirmationAmount;
        this.username = username;
    }

    // Getters and Setters.
    public Long getPaymentConfirmationId() {
        return paymentConfirmationId;
    }

    public Long getUserExpenseId() {
        return userExpenseId;
    }

    public Float getConfirmationAmount() {
        return confirmationAmount;
    }

    public String getUsername() {
        return username;
    }
}
