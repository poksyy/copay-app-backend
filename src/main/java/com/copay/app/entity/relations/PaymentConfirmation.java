package com.copay.app.entity.relations;

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
    private Long paymentConfirmationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false)
    private UserExpense userExpense;

    @Column(name = "sms_code")
    private String smsCode;

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmation_amount", nullable = false)
    private Float confirmationAmount;

    @Column(name = "confirmation_date", nullable = false)
    private LocalDateTime confirmationDate = LocalDateTime.now();

    public PaymentConfirmation() {}

	public Long getPaymentConfirmationId() {
		return paymentConfirmationId;
	}

	public void setPaymentConfirmationId(Long paymentConfirmationId) {
		this.paymentConfirmationId = paymentConfirmationId;
	}

	public UserExpense getUserExpense() {
		return userExpense;
	}

	public void setUserExpense(UserExpense userExpense) {
		this.userExpense = userExpense;
	}

	public Float getConfirmationAmount() {
		return confirmationAmount;
	}

	public void setConfirmationAmount(Float confirmationAmount) {
		this.confirmationAmount = confirmationAmount;
	}

	public LocalDateTime getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(LocalDateTime confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public Boolean getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(Boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public LocalDateTime getConfirmedAt() {
		return confirmedAt;
	}

	public void setConfirmedAt(LocalDateTime confirmedAt) {
		this.confirmedAt = confirmedAt;
	}
}
