package com.copay.app.dto.paymentconfirmation.request;

import jakarta.validation.constraints.NotBlank;

public class DeletePaymentConfirmationRequestDTO {

    @NotBlank(message = "Payment confirmation id must not be null")
    private Long paymentConfirmationId;

    // Getters and setters.
    public Long getPaymentConfirmationId() {
        return paymentConfirmationId;
    }

    public void setPaymentConfirmationId(Long paymentConfirmationId) {
        this.paymentConfirmationId = paymentConfirmationId;
    }
}
