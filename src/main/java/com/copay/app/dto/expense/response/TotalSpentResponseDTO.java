package com.copay.app.dto.expense.response;

public class TotalSpentResponseDTO {

    private Float totalSpent;

    public TotalSpentResponseDTO(Float totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Float getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Float totalSpent) {
        this.totalSpent = totalSpent;
    }
}