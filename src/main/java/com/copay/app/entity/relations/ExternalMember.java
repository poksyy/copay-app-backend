package com.copay.app.entity.relations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.copay.app.entity.Group;

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
@Table(name = "external_members")
public class ExternalMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "external_members_id")
	private Long externalMembersId;

	@Column(nullable = false)
	private String name;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;
	
	// LAZY fetch is used to avoid unnecessary loading of external members if not accessed.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	// Bidirectional mappings for debts
    @OneToMany(mappedBy = "debtorExternalMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserExpense> debtsAsDebtor = new ArrayList<>();

    @OneToMany(mappedBy = "creditorExternalMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserExpense> debtsAsCreditor = new ArrayList<>();
	
	// Empty constructor.
	public ExternalMember() {
		this.joinedAt = LocalDateTime.now();
	}

	// Getters and Setters.
	public Long getExternalMembersId() {
		return externalMembersId;
	}

	public void setExternalMembersId(Long externalMembersId) {
		this.externalMembersId = externalMembersId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

	public List<UserExpense> getDebtsAsDebtor() {
		return debtsAsDebtor;
	}

	public void setDebtsAsDebtor(List<UserExpense> debtsAsDebtor) {
		this.debtsAsDebtor = debtsAsDebtor;
	}

	public List<UserExpense> getDebtsAsCreditor() {
		return debtsAsCreditor;
	}

	public void setDebtsAsCreditor(List<UserExpense> debtsAsCreditor) {
		this.debtsAsCreditor = debtsAsCreditor;
	}
}
