package com.copay.app.service.expense;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.expense.response.UserExpenseDTO;
import com.copay.app.entity.relations.UserExpense;

import java.util.List;

public interface ExpenseService {

    ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request);

    List<ExpenseResponseDTO> getExpenses(Long groupId);

    List<UserExpenseDTO> getAllUserExpensesByGroupId(Long groupId);

    ExpenseResponseDTO getExpense(Long groupId, Long expenseId);

    MessageResponseDTO deleteExpenseByGroupAndId (Long groupId, Long expenseId);
}
