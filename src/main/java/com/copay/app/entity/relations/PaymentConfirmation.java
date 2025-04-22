package com.copay.app.entity.relations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_confirmations")
public class PaymentConfirmation {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_confirmation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false)
    private UserExpense userExpense;

    @Column(name = "confirmation_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal confirmationAmount;

    @Column(name = "confirmation_date", nullable = false)
    private LocalDateTime confirmationDate = LocalDateTime.now();

    public PaymentConfirmation() {
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserExpense getUserExpense() {
		return userExpense;
	}

	public void setUserExpense(UserExpense userExpense) {
		this.userExpense = userExpense;
	}

	public BigDecimal getConfirmationAmount() {
		return confirmationAmount;
	}

	public void setConfirmationAmount(BigDecimal confirmationAmount) {
		this.confirmationAmount = confirmationAmount;
	}

	public LocalDateTime getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(LocalDateTime confirmationDate) {
		this.confirmationDate = confirmationDate;
	}
}