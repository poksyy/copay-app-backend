package com.copay.app.service.expense;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ConfirmPaymentResponseDTO;
import com.copay.app.dto.expense.response.DebtorResponseDTO;
import com.copay.app.dto.expense.response.ExpenseResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;
import com.copay.app.exception.expense.ExpenseNotFoundException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.repository.expense.UserExpenseRepository;
import com.copay.app.repository.paymentconfirmation.PaymentConfirmationRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final PaymentConfirmationRepository paymentConfirmationRepository;

    ExpenseRepository expenseRepository;

    UserExpenseRepository userExpenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserExpenseRepository userExpenseRepository, PaymentConfirmationRepository paymentConfirmationRepository) {

        this.expenseRepository = expenseRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.paymentConfirmationRepository = paymentConfirmationRepository;
    }

    // Method triggered when creating a new group to initialize the expense management.
    public void initializeExpenseFromGroup(Group group, Float estimatedPrice, User userCreditor, ExternalMember externalCreditor) {
        // Create and save the main expense
        Expense expense = new Expense();
        expense.setGroupId(group);
        expense.setTotalAmount(estimatedPrice);
        setCreditor(expense, userCreditor, externalCreditor);
        expenseRepository.save(expense);

        // Calculate and save all individual expenses
        updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    // Method triggered when updating the group price to update the expenses.
    public void updateExpensePrice(Group group, Float newPrice, User payerUser, ExternalMember payerExternal) {

        // Get existing expense and update its price and payer.
        Expense expense = findExpenseByGroup(group);
        expense.setTotalAmount(newPrice);

        // Reset and set new payer.
        expense.setPaidByUser(null);
        expense.setPaidByExternalMember(null);
        setCreditor(expense, payerUser, payerExternal);

        expenseRepository.save(expense);
    }

    // Method triggered when updating registered users to update the expense.
    public void updateRegisteredUsers(Group group, User userCreditor, ExternalMember externalCreditor) {
        Expense expense = findExpenseByGroup(group);
        updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    // Method triggered when updating external members to update the expense.
    public void updateExternalMembers(Group group, User userCreditor, ExternalMember externalCreditor) {
        // Reuse the same method as updateRegisteredUsers since the logic is identical
        updateRegisteredUsers(group, userCreditor, externalCreditor);
    }

    public void deleteExpenseByGroupAndId(Long groupId, Long expenseId) {

        Optional<Expense> expenseOptional = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // Delete associated UserExpense records
            userExpenseRepository.deleteByExpenseId(expense);

            // TODO: Delete payment confirmations related to the expense
            // paymentConfirmationRepository.deleteByExpense(expense);

            // Finally, delete the expense itself
            expenseRepository.delete(expense);

        } else {
            throw new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId);
        }
    }

    @Override
    public ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request) {
        //TODO
        return null;
    }

    @Override
    public List<ExpenseResponseDTO> getExpenses(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId_GroupId(groupId);

        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException("No expenses found for group " + groupId);
        }

        return expenses.stream()
                .map(this::mapToExpenseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseResponseDTO getExpense(Long groupId, Long expenseId) {
        Expense expense = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId));

        return mapToExpenseResponseDTO(expense);
    }

    @Override
    public ConfirmPaymentResponseDTO confirmPayment(Long expenseId, Long userExpenseId) {
        // TODO
        return null;
    }

    // Sets the creditor (payer) for an expense
    private void setCreditor(Expense expense, User userCreditor, ExternalMember externalCreditor) {

        if (userCreditor != null) {

            expense.setPaidByUser(userCreditor);

        } else if (externalCreditor != null) {
            expense.setPaidByExternalMember(externalCreditor);
        }
    }


    // Finds an expense by group or throws an exception.
    private Expense findExpenseByGroup(Group group) {

        return expenseRepository.findByGroupId(group)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense of the group " + group.getGroupId() + " not found"));
    }

    // Updates expense distribution among all members.
    private void updateExpenseDistribution(Group group, Expense expense, User userCreditor, ExternalMember externalCreditor) {

        // Get all current members (registered users and external members).
        List<User> memberUsers = group.getRegisteredMembers().stream()
                .map(gm -> gm.getId().getUser())
                .toList();
        List<ExternalMember> externalMembers = new ArrayList<>(group.getExternalMembers());

        // Remove debts from users/external members who are no longer in the group.
        List<UserExpense> existingExpenses = userExpenseRepository.findByExpenseId(expense);
        List<UserExpense> toRemove = new ArrayList<>();

        // Iterate over existing debts and identify if the debtor (user or external member) is no longer in the group.
        for (UserExpense ue : existingExpenses) {
            boolean userStillExists = ue.getDebtorUser() != null && memberUsers.stream()
                    .anyMatch(u -> u.getUserId().equals(ue.getDebtorUser().getUserId()));
            boolean externalStillExists = ue.getDebtorExternalMember() != null && externalMembers.stream()
                    .anyMatch(em -> em.getExternalMembersId().equals(ue.getDebtorExternalMember().getExternalMembersId()));

            // If neither the user nor the external member exists in the group, mark this debt for removal.
            if (!userStillExists && !externalStillExists) {
                toRemove.add(ue);
            }
        }

        // Delete debts from users or external members who are no longer part of the group.
        userExpenseRepository.deleteAll(toRemove);

        // Calculate total debtors (all users and external members except the creditor).
        int totalDebtors = getTotalDebtors(group, userCreditor, externalCreditor);

        if (totalDebtors == 0) {
            throw new IllegalArgumentException("There are no debtors to split the expense.");
        }

        // Calculate the amount each debtor should pay.
        BigDecimal amountPerDebtor = BigDecimal.valueOf(expense.getTotalAmount())
                .divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP);

        // List to store all updated debts.
        List<UserExpense> updatedExpenses = new ArrayList<>();

        // Process registered users (members of the group).
        for (User user : memberUsers) {
            // Skip the creditor if it's the same user.
            if (userCreditor != null && user.getUserId().equals(userCreditor.getUserId())) continue;

            // Find existing debt or create a new one if it doesn't exist
            UserExpense userExpense = userExpenseRepository.findByExpenseIdAndDebtorUser(expense, user)
                    .orElseGet(UserExpense::new);

            // Set the expense for the debt.
            userExpense.setExpense(expense);
            // Set the user as the debtor.
            userExpense.setDebtorUser(user);
            userExpense.setAmount(amountPerDebtor.floatValue());  // Set the amount this user needs to pay

            // Set the creditor (either a user or an external member).
            if (userCreditor != null) {

                userExpense.setCreditorUser(userCreditor);
                userExpense.setCreditorExternalMember(null);
            } else {
                userExpense.setCreditorUser(null);
                userExpense.setCreditorExternalMember(externalCreditor);
            }

            updatedExpenses.add(userExpense);
        }

        // Process external members (those outside the registered user list).
        for (ExternalMember externalMember : externalMembers) {

            // Skip the creditor if it's the same external member.
            if (externalCreditor != null && externalMember.getExternalMembersId().equals(externalCreditor.getExternalMembersId()))
                continue;

            // Find existing debt or create a new one if it doesn't exist.
            UserExpense userExpense = userExpenseRepository.findByExpenseIdAndDebtorExternalMember(expense, externalMember)
                    .orElseGet(UserExpense::new);

            // Set the expense for the debt.
            userExpense.setExpense(expense);
            // Set the external member as the debtor.
            userExpense.setDebtorExternalMember(externalMember);
            userExpense.setAmount(amountPerDebtor.floatValue());  // Set the amount this external member needs to pay

            // Set the creditor (either a user or an external member).
            if (userCreditor != null) {

                userExpense.setCreditorUser(userCreditor);
                userExpense.setCreditorExternalMember(null);
            } else {
                userExpense.setCreditorUser(null);
                userExpense.setCreditorExternalMember(externalCreditor);
            }

            updatedExpenses.add(userExpense);
        }

        // Save all the updated debts to the repository.
        userExpenseRepository.saveAll(updatedExpenses);
    }

    // Count total debtors (excluding the creditor).
    private int getTotalDebtors(Group group, User userCreditor, ExternalMember externalCreditor) {
        List<User> memberUsers = group.getRegisteredMembers().stream()
                .map(gm -> gm.getId().getUser())
                .toList();
        List<ExternalMember> externalMembers = new ArrayList<>(group.getExternalMembers());

        return (int) Stream.concat(
                memberUsers.stream().filter(u -> userCreditor == null || !u.getUserId().equals(userCreditor.getUserId())),
                externalMembers.stream().filter(em -> externalCreditor == null || !em.getExternalMembersId().equals(externalCreditor.getExternalMembersId()))
        ).count();
    }

    private ExpenseResponseDTO mapToExpenseResponseDTO(Expense expense) {
        // Get all user expenses associated with the expense
        List<UserExpense> userExpenses = userExpenseRepository.findByExpenseId(expense);

        // Create separate lists for registered members and external members
        List<DebtorResponseDTO> registeredMembers = new ArrayList<>();
        List<DebtorResponseDTO> externalMembers = new ArrayList<>();

        // Iterate through the user expenses to separate registered members from external members
        for (UserExpense ue : userExpenses) {
            Float amount = ue.getAmount();
            Long creditorUserId = Optional.ofNullable(ue.getCreditorUser()).map(User::getUserId).orElse(null);
            Long creditorExternalMemberId = Optional.ofNullable(ue.getCreditorExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

            // Create the DTO depending on whether it is a registered user or an external member
            DebtorResponseDTO dto = createDebtorResponseDTO(ue, creditorUserId, creditorExternalMemberId, amount);

            // Separate into registered members and external members
            if (ue.getDebtorUser() != null) {
                registeredMembers.add(dto);
            } else if (ue.getDebtorExternalMember() != null) {
                externalMembers.add(dto);
            }
        }

        // Get creditor data (who paid)
        Long creditorUserId = Optional.ofNullable(expense.getPaidByUser()).map(User::getUserId).orElse(null);
        Long creditorExternalMemberId = Optional.ofNullable(expense.getPaidByExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

        // Create the final DTO with debt information
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

    // Helper method to create a debtor DTO (user or external member)
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