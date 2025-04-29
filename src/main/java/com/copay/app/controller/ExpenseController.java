package com.copay.app.controller;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.service.ValidationService;
import com.copay.app.service.expense.ExpenseServiceImpl;
import com.copay.app.validation.ValidationErrorResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseServiceImpl expenseService;

    // Constructor
    public ExpenseController(ExpenseServiceImpl expenseService) {
        this.expenseService = expenseService;
    }

    // Endpoint to create a new expense.
    @PostMapping("/{groupId}")
    public ResponseEntity<?> createExpense(Long groupId, @RequestBody @Valid CreateExpenseRequestDTO createExpenseRequestDTO,
                                         BindingResult result) {

        ValidationErrorResponse validationResponse = ValidationService.validate(result);

        // Validates the DTO annotations.
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        ExpenseResponseDTO expenseResponseDTO = expenseService.createExpense(groupId, createExpenseRequestDTO);

        return ResponseEntity.ok(expenseResponseDTO);
    }

    // Endpoint to retrieve all the expenses.
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getExpenses(@PathVariable Long groupId, BindingResult result) {

        ValidationErrorResponse validationResponse = ValidationService.validate(result);

        // Validates the DTO annotations.
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        List<ExpenseResponseDTO> getExpenses = expenseService.getExpenses(groupId);

       return ResponseEntity.ok(getExpenses);
    }

    // Endpoint to retrieve details of one expense.
    @GetMapping("/{groupId}/{expenseId}")
    public ResponseEntity<?> getExpense(@PathVariable Long groupId, Long expenseId, BindingResult result) {

        ValidationErrorResponse validationResponse = ValidationService.validate(result);

        // Validates the DTO annotations.
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        ExpenseResponseDTO getExpense = expenseService.getExpense(groupId, expenseId);

        return ResponseEntity.ok(getExpense);
    }

    @DeleteMapping("/{groupId}/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long groupId, @PathVariable Long expenseId) {

        expenseService.deleteExpenseByGroupAndId(groupId, expenseId);

        return ResponseEntity.ok("Expense deleted successfully.");
    }

}
