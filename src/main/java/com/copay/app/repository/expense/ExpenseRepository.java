package com.copay.app.repository.expense;

import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByGroup(Group group);

    Optional<Expense> findByIdAndGroupId(Long expenseId, Long groupId);
}