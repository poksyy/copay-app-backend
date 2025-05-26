package com.copay.app.service.expense;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.expense.response.TotalDebtResponseDTO;
import com.copay.app.dto.expense.response.TotalSpentResponseDTO;
import com.copay.app.dto.expense.response.UserExpenseDTO;

import java.util.List;

public interface ExpenseService {

    ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request);

    List<ExpenseResponseDTO> getExpenses(Long groupId);

    List<UserExpenseDTO> getAllUserExpensesByGroupId(Long groupId);

    ExpenseResponseDTO getExpense(Long groupId, Long expenseId);

    MessageResponseDTO deleteExpenseByGroupAndId (Long groupId, Long expenseId);

    /**
     * Get the total debt amount for a user across all groups
     * @param userId the ID of the user
     * @return the total debt amount as a TotalAmountResponseDTO
     */
    TotalDebtResponseDTO getTotalUserDebt(Long userId);

    /**
     * Get the total amount spent by a user across all groups
     * @param userId the ID of the user
     * @return the total amount spent as a TotalAmountResponseDTO
     */
    TotalSpentResponseDTO getTotalUserSpent(Long userId);
}
