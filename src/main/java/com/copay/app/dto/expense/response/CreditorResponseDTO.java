package com.copay.app.dto.expense.response;

public class CreditorResponseDTO {
    private Long creditorId;
    private String creditorName;

    public CreditorResponseDTO(Long creditorId, String creditorName) {
        this.creditorId = creditorId;
        this.creditorName = creditorName;
    }

    public Long getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(Long creditorId) {
        this.creditorId = creditorId;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorNameId(String creditorName) {
        this.creditorName = creditorName;
    }
}
