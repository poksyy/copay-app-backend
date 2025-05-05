package com.copay.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "expenses")
public class Expense {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group groupId;

    @Column(name = "total_amount", nullable = false)
    private Float totalAmount;

    @ManyToOne
    @JoinColumn(name = "paid_by_user_id")
    private User paidByUser;

    @ManyToOne
    @JoinColumn(name = "paid_by_external_member_id")
    private ExternalMember paidByExternalMember;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "expenseId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserExpense> userExpenses = new ArrayList<>();

    public Expense() {

	}

    // Getters and Setters.
	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}

	public Group getGroupId() {
		return groupId;
	}

	public void setGroupId(Group groupId) {
		this.groupId = groupId;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public User getPaidByUser() {
		return paidByUser;
	}

	public void setPaidByUser(User paidByUser) {
		this.paidByUser = paidByUser;
	}

	public ExternalMember getPaidByExternalMember() {
		return paidByExternalMember;
	}

	public void setPaidByExternalMember(ExternalMember paidByExternalMember) {
		this.paidByExternalMember = paidByExternalMember;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<UserExpense> getUserExpenses() {
		return userExpenses;
	}

	public void setUserExpenses(List<UserExpense> userExpenses) {
		this.userExpenses = userExpenses;
	}

}
