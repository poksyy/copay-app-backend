package com.copay.app.dto.paymentconfirmation.response;

import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long paymentConfirmationId;

    private Long userExpenseId;

    private Float confirmationAmount;

    private LocalDateTime confirmationDate;

    private Boolean isConfirmed;

    private LocalDateTime confirmedAt;

    private String username;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(Long paymentConfirmationId, Long userExpenseId, Float confirmationAmount, LocalDateTime confirmationDate, Boolean isConfirmed, LocalDateTime confirmedAt, String username) {
        
        this.paymentConfirmationId = paymentConfirmationId;
        this.userExpenseId = userExpenseId;
        this.confirmationAmount = confirmationAmount;
        this.confirmationDate = confirmationDate;
        this.isConfirmed = isConfirmed;
        this.confirmedAt = confirmedAt;
        this.username = username;
    }

    // Getters and Setters.
    public Long getPaymentConfirmationId() {
        return paymentConfirmationId;
    }

    public void setPaymentConfirmationId(Long paymentConfirmationId) {
        this.paymentConfirmationId = paymentConfirmationId;
    }

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

    public LocalDateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}
}
