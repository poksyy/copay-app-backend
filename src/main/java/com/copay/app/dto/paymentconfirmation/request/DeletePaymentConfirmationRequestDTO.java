package com.copay.app.dto.paymentconfirmation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeletePaymentConfirmationRequestDTO {

    @NotNull(message = "Payment confirmation id must not be null")
    private Long paymentConfirmationId;

    // Getters and setters.
    public Long getPaymentConfirmationId() {
        return paymentConfirmationId;
    }

    public void setPaymentConfirmationId(Long paymentConfirmationId) {
        this.paymentConfirmationId = paymentConfirmationId;
    }
}
