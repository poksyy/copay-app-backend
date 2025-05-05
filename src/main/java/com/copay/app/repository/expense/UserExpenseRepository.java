package com.copay.app.repository.expense;

import com.copay.app.entity.Expense;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserExpenseRepository extends JpaRepository<UserExpense, Long> {

    void deleteByExpenseId(Expense expenseId);

    Optional<UserExpense> findByExpenseIdAndDebtorUser(Expense expenseId, User user);

    Optional<UserExpense> findByExpenseIdAndDebtorExternalMember(Expense expenseId, ExternalMember externalMember);

    List<UserExpense> findByExpenseId(Expense expenseId);

}