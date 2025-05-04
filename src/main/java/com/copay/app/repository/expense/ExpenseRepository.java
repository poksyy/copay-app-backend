package com.copay.app.repository.expense;

import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByGroupId(Group groupId);

    Optional<Expense> findByExpenseIdAndGroupId_GroupId(Long expenseId, Long groupId);

    List<Expense> findAllByGroupId(Group groupId);

    List<Expense> findByGroupId_GroupId(Long groupId);

}