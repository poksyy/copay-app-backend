package com.copay.app.dto.expense.response;

public class UserExpenseDTO {
    private Long userExpenseId;
    private Long debtorUserId;
    private Float amount;

    public UserExpenseDTO(Long userExpenseId, Long debtorUserId, Float amount) {
        this.userExpenseId = userExpenseId;
        this.debtorUserId = debtorUserId;
        this.amount = amount;
    }

    // Getters
    public Long getUserExpenseId() {
        return userExpenseId;
    }

    public Long getDebtorUserId() {
        return debtorUserId;
    }

    public Float getAmount() {
        return amount;
    }
}