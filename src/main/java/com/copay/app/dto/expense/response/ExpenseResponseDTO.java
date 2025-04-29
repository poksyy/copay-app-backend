package com.copay.app.dto.expense.response;

public class ExpenseResponseDTO {

    private Long id;
    private Float totalAmount;
    private Long groupId;

    // Constructor
    public ExpenseResponseDTO(Long id, Float totalAmount, Long groupId) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.groupId = groupId;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "ExpenseResponseDTO{" +
                "id=" + id +
                ", totalAmount=" + totalAmount +
                ", groupId=" + groupId +
                '}';
    }
}
