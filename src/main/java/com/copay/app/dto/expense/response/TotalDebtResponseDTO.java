package com.copay.app.dto.expense.response;

public class TotalDebtResponseDTO {

    private Float totalDebt;

    public TotalDebtResponseDTO(Float totalDebt) {
        this.totalDebt = totalDebt;
    }

    public Float getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(Float totalSpent) {
        this.totalDebt = totalSpent;
    }
}