package com.copay.app.dto.paymentconfirmation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RequestPaymentConfirmationRequestDTO {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Expense ID is required")
    private Long expenseId;

    @NotNull(message = "User Expense ID is required")
    private Long userExpenseId;

    @NotNull(message = "Confirmation amount is required")
    @Positive(message = "Confirmation amount must be positive")
    private Float confirmationAmount;

    // Optional SMS code field (can be null)
    private String smsCode;

    public RequestPaymentConfirmationRequestDTO() {
    }

    public RequestPaymentConfirmationRequestDTO(Long groupId, Long expenseId, Long userExpenseId, Float confirmationAmount, String smsCode) {
        this.groupId = groupId;
        this.expenseId = expenseId;
        this.userExpenseId = userExpenseId;
        this.confirmationAmount = confirmationAmount;
        this.smsCode = smsCode;
    }

    // Getters and Setters.
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
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

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
