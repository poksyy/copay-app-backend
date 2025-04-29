package com.copay.app.dto.expense.response;

import com.fasterxml.jackson.annotation.JsonInclude;

// Caution: Exclude null fields from the JSON response and keep it clean.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebtorResponseDTO {

    private Long debtorUserMemberId;
    private Long debtorExternalMemberId;
    private Float amount;
    private Long creditorUserId;
    private Long creditorExternalMemberId;

    public DebtorResponseDTO(Long debtorUserMemberId, Long debtorExternalMemberId,
                             Long creditorUserId, Long creditorExternalMemberId,
                             Float amount) {
        this.debtorUserMemberId = debtorUserMemberId;
        this.debtorExternalMemberId = debtorExternalMemberId;
        this.amount = amount;
        this.creditorUserId = creditorUserId;
        this.creditorExternalMemberId = creditorExternalMemberId;
    }

    // Getters y setters
    public Long getDebtorUserId() {
        return debtorUserMemberId;
    }

    public void setDebtorUserId(Long debtorUserMemberId) {
        this.debtorUserMemberId = debtorUserMemberId;
    }

    public Long getDebtorExternalMemberId() {
        return debtorExternalMemberId;
    }

    public void setDebtorExternalMemberId(Long debtorExternalMemberId) {
        this.debtorExternalMemberId = debtorExternalMemberId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Long getCreditorUserId() {
        return creditorUserId;
    }

    public void setCreditorUserId(Long creditorUserId) {
        this.creditorUserId = creditorUserId;
    }

    public Long getCreditorExternalMemberId() {
        return creditorExternalMemberId;
    }

    public void setCreditorExternalMemberId(Long creditorExternalMemberId) {
        this.creditorExternalMemberId = creditorExternalMemberId;
    }
}
