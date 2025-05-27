package com.copay.app.controller;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.dto.expense.response.TotalDebtResponseDTO;
import com.copay.app.dto.expense.response.TotalSpentResponseDTO;
import com.copay.app.dto.expense.response.UserExpenseDTO;
import com.copay.app.service.expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    // Constructor.
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // Endpoint to retrieve all the expenses.
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getAllExpensesByGroupId(@PathVariable Long groupId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        List<ExpenseResponseDTO> getExpenses = expenseService.getExpenses(groupId, token);

       return ResponseEntity.ok(getExpenses);
    }

    // Endpoint to retrieve details of one expense.
    @GetMapping("/{groupId}/{expenseId}")
    public ResponseEntity<?> getExpenseByGroupIdAndExternalId(@PathVariable Long groupId, @PathVariable Long expenseId){

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        ExpenseResponseDTO getExpense = expenseService.getExpenseByGroupIdAndExternalId(groupId, expenseId, token);

        return ResponseEntity.ok(getExpense);
    }

    // Endpoint to user expenses of one group.
    @GetMapping("/{groupId}/user-expenses")
    public ResponseEntity<List<UserExpenseDTO>> getAllUserExpensesByGroupId(@PathVariable Long groupId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        List<UserExpenseDTO> userExpenses = expenseService.getAllUserExpensesByGroupId(groupId, token);

        return ResponseEntity.ok(userExpenses);
    }

    // TODO: Implement this method of creating expenses when implementing more than one expense per group.
    @PostMapping("/{groupId}")
    public ResponseEntity<?> createExpense(@PathVariable Long groupId, @RequestBody @Valid CreateExpenseRequestDTO createExpenseRequestDTO) {

        ExpenseResponseDTO expenseResponseDTO = expenseService.createExpense(groupId, createExpenseRequestDTO);

        return ResponseEntity.ok(expenseResponseDTO);
    }

    // TODO: Implement delete expense when more than one expense in one group is implemented.
    @DeleteMapping("/{groupId}/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long groupId, @PathVariable Long expenseId) {

        MessageResponseDTO messageResponseDTO = expenseService.deleteExpenseByGroupAndId(groupId, expenseId);

        return ResponseEntity.ok("messageResponseDTO");
    }

    // Endpoint to get the total debt amount for a user across all groups
    @GetMapping("/user/{userId}/total-debt")
    public ResponseEntity<TotalDebtResponseDTO> getTotalUserDebt(@PathVariable Long userId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        TotalDebtResponseDTO totalDebtResponseDTO = expenseService.getTotalUserDebt(userId, token);

        return ResponseEntity.ok(totalDebtResponseDTO);
    }

    // Endpoint to get the total amount spent by a user across all groups
    @GetMapping("/user/{userId}/total-spent")
    public ResponseEntity<TotalSpentResponseDTO> getTotalUserSpent(@PathVariable Long userId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        TotalSpentResponseDTO totalSpentResponseDTO = expenseService.getTotalUserSpent(userId, token);

        return ResponseEntity.ok(totalSpentResponseDTO);
    }
}
