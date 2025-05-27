package com.copay.app.service.expense;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.*;
import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;
import com.copay.app.exception.expense.ExpenseNotFoundException;
import com.copay.app.exception.user.UserPermissionException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.repository.expense.UserExpenseRepository;
import com.copay.app.repository.paymentconfirmation.PaymentConfirmationRepository;

import com.copay.app.service.JwtService;
import com.copay.app.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final PaymentConfirmationRepository paymentConfirmationRepository;

    private final ExpenseRepository expenseRepository;

    private final UserExpenseRepository userExpenseRepository;

    private final JwtService jwtService;
    private final UserQueryService userQueryService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserExpenseRepository userExpenseRepository,
                              PaymentConfirmationRepository paymentConfirmationRepository, JwtService jwtService, UserQueryService userQueryService) {

        this.expenseRepository = expenseRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.paymentConfirmationRepository = paymentConfirmationRepository;
        this.jwtService = jwtService;
        this.userQueryService = userQueryService;
    }

    @Override
    @Transactional(readOnly = true)
    public TotalDebtResponseDTO getTotalUserDebt(Long userId, String token) {

        // Extract the phone number from the token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        User user = userQueryService.getUserByPhone(phoneNumber);

        if (!user.getUserId().equals(userId)) {

            throw new UserPermissionException("User does not match the user in the token.");
        }

        Float totalDebt = userExpenseRepository.getTotalDebtByUserId(userId);

        return new TotalDebtResponseDTO(totalDebt != null ? totalDebt : 0.0f);
    }

    @Override
    @Transactional(readOnly = true)
    public TotalSpentResponseDTO getTotalUserSpent(Long userId, String token) {

        // Extract the phone number from the token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        User user = userQueryService.getUserByPhone(phoneNumber);

        if (!user.getUserId().equals(userId)) {

            throw new UserPermissionException("User does not match the user in the token.");
        }

        Float totalSpent = paymentConfirmationRepository.getTotalSpentByUserId(userId);

        return new TotalSpentResponseDTO(totalSpent != null ? totalSpent : 0.0f);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpenses(Long groupId, String token) {

        List<Expense> expenses = expenseRepository.findByGroupId_GroupId(groupId);

        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException("No expenses found for group " + groupId);
        }

        return expenses.stream()
                .map(this::mapToExpenseResponseDTO)
                .collect(Collectors.toList());
    }

    // Find all user expenses of a group by groupid.
    @Override
    public List<UserExpenseDTO> getAllUserExpensesByGroupId(Long groupId, String token) {

        return userExpenseRepository.findAllByGroupId(groupId);
    }

    // Find an expense by id of a specific group by id.
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseByGroupIdAndExternalId(Long groupId, Long expenseId, String token) {

        Expense expense = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId));

        return mapToExpenseResponseDTO(expense);
    }

    // TODO: Implement this method of creating expenses when implementing more than one expense per group.
    @Override
    @Transactional
    public ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request) {
        return null;
    }

    @Override
    @Transactional
    // Delete an expense of a specific group by retrieving expense id and group id.
    public MessageResponseDTO deleteExpenseByGroupAndId(Long groupId, Long expenseId) {

        Optional<Expense> expenseOptional = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // Delete associated UserExpense records.
            userExpenseRepository.deleteByExpenseId(expense);

            // Delete payment confirmations related to the expense.
            paymentConfirmationRepository.deleteByUserExpense_ExpenseId(expense);

            // Finally, delete the expense itself.
            expenseRepository.delete(expense);

        } else {
            throw new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId);
        }

        // Return a success message.
        return new MessageResponseDTO("Group deleted successfully.");
    }

    private ExpenseResponseDTO mapToExpenseResponseDTO(Expense expense) {

        // Get all user expenses associated with the expense.
        List<UserExpense> userExpenses = userExpenseRepository.findByExpenseId(expense);

        // Create separate lists for registered members and external members.
        List<DebtorResponseDTO> registeredMembers = new ArrayList<>();
        List<DebtorResponseDTO> externalMembers = new ArrayList<>();

        // Iterate through the user expenses to separate registered members from external members.
        for (UserExpense userExpense : userExpenses) {

            Float amount = userExpense.getAmount();

            Long creditorUserId = Optional.ofNullable(userExpense.getCreditorUser()).map(User::getUserId).orElse(null);
            Long creditorExternalMemberId = Optional.ofNullable(userExpense.getCreditorExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

            // Create the DTO depending on whether it is a registered user or an external member.
            DebtorResponseDTO dto = createDebtorResponseDTO(userExpense, creditorUserId, creditorExternalMemberId, amount);

            // Separate into registered members and external members.
            if (userExpense.getDebtorUser() != null) {
                registeredMembers.add(dto);
            } else if (userExpense.getDebtorExternalMember() != null) {
                externalMembers.add(dto);
            }
        }

        // Get creditor data (who paid).
        Long creditorUserId = Optional.ofNullable(expense.getPaidByUser()).map(User::getUserId).orElse(null);
        Long creditorExternalMemberId = Optional.ofNullable(expense.getPaidByExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

        // Create the final DTO with debt information.
        return new ExpenseResponseDTO(
                expense.getExpenseId(),
                expense.getTotalAmount(),
                expense.getGroupId().getGroupId(),
                creditorUserId,
                creditorExternalMemberId,
                registeredMembers,
                externalMembers
        );
    }

    // Helper method to create a debtor DTO (user or external member).
    private DebtorResponseDTO createDebtorResponseDTO(UserExpense ue, Long creditorUserId, Long creditorExternalMemberId, Float amount) {

        Long debtorUserId = Optional.ofNullable(ue.getDebtorUser()).map(User::getUserId).orElse(null);
        Long debtorExternalMemberId = Optional.ofNullable(ue.getDebtorExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

        return new DebtorResponseDTO(
                debtorUserId,
                debtorExternalMemberId,
                creditorUserId,
                creditorExternalMemberId,
                amount
        );
    }
}