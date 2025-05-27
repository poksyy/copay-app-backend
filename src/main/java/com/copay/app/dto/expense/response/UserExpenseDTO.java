package com.copay.app.dto.expense.response;

public class UserExpenseDTO {
    private Long userExpenseId;
    private Long debtorUserId;
    private Long debtorExternalId;
    private Float amount;

    public UserExpenseDTO(Long userExpenseId, Long debtorUserId, Long debtorExternalId, Float amount) {
        this.userExpenseId = userExpenseId;
        this.debtorUserId = debtorUserId;
        this.debtorExternalId = debtorExternalId;
        this.amount = amount;
    }

    // Getters
    public Long getUserExpenseId() {
        return userExpenseId;
    }

    public Long getDebtorUserId() {
        return debtorUserId;
    }

    public Long getDebtorExternalId() {
        return debtorExternalId;
    }

    public Float getAmount() {
        return amount;
    }
}