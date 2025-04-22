package com.copay.app.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.UserExpense;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "phone_prefix", nullable = true)
	private String phonePrefix;

	@Column(unique = true, nullable = true)
	private String phoneNumber;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "is_completed", columnDefinition = "TINYINT(1)")
	private boolean isCompleted = false;

	// CascadeType.ALL propagates all persistence operations to GroupMember.
	// orphanRemoval ensures deletion of GroupMember when removed from the Set.
	// "id.user" is used because GroupMember has a composite key via the @Embeddable GroupMemberId.
	@OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GroupMember> groupMembers = new HashSet<>();

    // Expenses paid by this user
    @OneToMany(mappedBy = "paidByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expense> paidExpenses = new HashSet<>();

    // UserExpenses where this user is the debtor
    @OneToMany(mappedBy = "debtorUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserExpense> debtsOwed = new HashSet<>();

    // UserExpenses where this user is the creditor
    @OneToMany(mappedBy = "creditorUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserExpense> debtsReceivable = new HashSet<>();
	
	// Constructor for fake data.
	public User(String username, String email, String password, String phonePrefix, String phoneNumber) {
		this.username = username;
		this.email = email;
		this.phonePrefix = phonePrefix;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.createdAt = LocalDateTime.now();
		this.isCompleted = true;
	}

	// Empty constructor.
	public User() {

	}

	// Getters and Setters.
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean completed) {
		isCompleted = completed;
	}

	public Set<GroupMember> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set<Expense> getPaidExpenses() {
		return paidExpenses;
	}

	public void setPaidExpenses(Set<Expense> paidExpenses) {
		this.paidExpenses = paidExpenses;
	}

	public Set<UserExpense> getDebtsOwed() {
		return debtsOwed;
	}

	public void setDebtsOwed(Set<UserExpense> debtsOwed) {
		this.debtsOwed = debtsOwed;
	}

	public Set<UserExpense> getDebtsReceivable() {
		return debtsReceivable;
	}

	public void setDebtsReceivable(Set<UserExpense> debtsReceivable) {
		this.debtsReceivable = debtsReceivable;
	}
}