package com.copay.app.dto.expense.response;

import java.util.List;

public class ExpenseResponseDTO {
    private Long id;
    private Float totalAmount;
    private Long groupId;

    private Long creditorUserId;
    private Long creditorExternalMemberId;

    private List<DebtorResponseDTO> registeredMembers;
    private List<DebtorResponseDTO> externalMembers;

    public ExpenseResponseDTO(Long id, Float totalAmount, Long groupId, Long creditorUserId, Long creditorExternalMemberId,
                              List<DebtorResponseDTO> registeredMembers, List<DebtorResponseDTO> externalMembers) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.groupId = groupId;
        this.creditorUserId = creditorUserId;
        this.creditorExternalMemberId = creditorExternalMemberId;
        this.registeredMembers = registeredMembers;
        this.externalMembers = externalMembers;
    }

    // Getters and setters.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public List<DebtorResponseDTO> getRegisteredMembers() {
        return registeredMembers;
    }

    public void setRegisteredMembers(List<DebtorResponseDTO> registeredMembers) {
        this.registeredMembers = registeredMembers;
    }

    public List<DebtorResponseDTO> getExternalMembers() {
        return externalMembers;
    }

    public void setExternalMembers(List<DebtorResponseDTO> externalMembers) {
        this.externalMembers = externalMembers;
    }
}