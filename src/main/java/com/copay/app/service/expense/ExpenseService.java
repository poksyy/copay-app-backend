package com.copay.app.service.expense;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.dto.MessageResponseDTO;

import java.util.List;

public interface ExpenseService {

    ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request);

    List<ExpenseResponseDTO> getExpenses(Long groupId);

    ExpenseResponseDTO getExpense(Long groupId, Long expenseId);

    MessageResponseDTO deleteExpenseByGroupAndId (Long groupId, Long expenseId);
}
