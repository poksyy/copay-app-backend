package com.copay.app.entity.relations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.copay.app.entity.Expense;
import com.copay.app.entity.User;

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

@Entity
@Table(name = "user_expenses")
public class UserExpense {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "debtor_user_id")
    private User debtorUser;

    @ManyToOne
    @JoinColumn(name = "debtor_external_member_id")
    private ExternalMember debtorExternalMember;

    @ManyToOne
    @JoinColumn(name = "creditor_user_id")
    private User creditorUser;

    @ManyToOne
    @JoinColumn(name = "creditor_external_member_id")
    private ExternalMember creditorExternalMember;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @OneToMany(mappedBy = "userExpense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentConfirmation> confirmations = new ArrayList<>();    
    
    public UserExpense() {

	}

    // Getters and Setters.
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public User getDebtorUser() {
		return debtorUser;
	}

	public void setDebtorUser(User debtorUser) {
		this.debtorUser = debtorUser;
	}

	public ExternalMember getDebtorExternalMember() {
		return debtorExternalMember;
	}

	public void setDebtorExternalMember(ExternalMember debtorExternalMember) {
		this.debtorExternalMember = debtorExternalMember;
	}

	public User getCreditorUser() {
		return creditorUser;
	}

	public void setCreditorUser(User creditorUser) {
		this.creditorUser = creditorUser;
	}

	public ExternalMember getCreditorExternalMember() {
		return creditorExternalMember;
	}

	public void setCreditorExternalMember(ExternalMember creditorExternalMember) {
		this.creditorExternalMember = creditorExternalMember;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	

	public List<PaymentConfirmation> getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(List<PaymentConfirmation> confirmations) {
		this.confirmations = confirmations;
	}
}
