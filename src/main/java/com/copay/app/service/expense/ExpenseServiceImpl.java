package com.copay.app.service.expense;

import com.copay.app.dto.expense.request.CreateExpenseRequestDTO;
import com.copay.app.dto.expense.response.ConfirmPaymentResponseDTO;
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


    public void initializeExpenseFromGroup(Group group, Float estimatedPrice, User userCreditor, ExternalMember externalCreditor) {
        // Create and save the main expense
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setTotalAmount(estimatedPrice);
        setCreditor(expense, userCreditor, externalCreditor);
        expenseRepository.save(expense);

        // Calculate and save all individual expenses
        updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    public void updateExpensePrice(Group group, Float newPrice, User payerUser, ExternalMember payerExternal) {
        // Get existing expense and update its price and payer
        Expense expense = findExpenseByGroup(group);
        expense.setTotalAmount(newPrice);

        // Reset and set new payer
        expense.setPaidByUser(null);
        expense.setPaidByExternalMember(null);
        setCreditor(expense, payerUser, payerExternal);

        expenseRepository.save(expense);
    }

    public void updateRegisteredUsers(Group group, User userCreditor, ExternalMember externalCreditor) {
        Expense expense = findExpenseByGroup(group);
        updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    public void updateExternalMembers(Group group, User userCreditor, ExternalMember externalCreditor) {
        // Reuse the same method as updateRegisteredUsers since the logic is identical
        updateRegisteredUsers(group, userCreditor, externalCreditor);
    }

    public void deleteExpenseByGroupAndId(Long groupId, Long expenseId) {
        Optional<Expense> expenseOptional = expenseRepository.findByIdAndGroupId(expenseId, groupId);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // Delete associated UserExpense records
            userExpenseRepository.deleteByExpense(expense);

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
        //TODO
        return null;
    }

    @Override
    public ExpenseResponseDTO getExpense(Long groupId, Long expenseId) {
        //TODO
        return null;
    }

    @Override
    public ConfirmPaymentResponseDTO confirmPayment(Long expenseId, Long userExpenseId) {
        //TODO
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


    // Finds an expense by group or throws an exception
    private Expense findExpenseByGroup(Group group) {
        return expenseRepository.findByGroup(group)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense of the group " + group.getGroupId() + " not found"));
    }

    // Updates expense distribution among all members
    private void updateExpenseDistribution(Group group, Expense expense, User userCreditor, ExternalMember externalCreditor) {
        // Get all current members (registered users and external members)
        List<User> memberUsers = group.getRegisteredMembers().stream()
                .map(gm -> gm.getId().getUser())
                .toList();
        List<ExternalMember> externalMembers = new ArrayList<>(group.getExternalMembers());

        // Remove debts from users/external members who are no longer in the group
        List<UserExpense> existingExpenses = userExpenseRepository.findByExpense(expense);
        List<UserExpense> toRemove = new ArrayList<>();

        // Iterate over existing debts and identify if the debtor (user or external member) is no longer in the group
        for (UserExpense ue : existingExpenses) {
            boolean userStillExists = ue.getDebtorUser() != null && memberUsers.stream()
                    .anyMatch(u -> u.getUserId().equals(ue.getDebtorUser().getUserId()));
            boolean externalStillExists = ue.getDebtorExternalMember() != null && externalMembers.stream()
                    .anyMatch(em -> em.getExternalMembersId().equals(ue.getDebtorExternalMember().getExternalMembersId()));

            // If neither the user nor the external member exists in the group, mark this debt for removal
            if (!userStillExists && !externalStillExists) {
                toRemove.add(ue);
            }
        }

        // Delete debts from users or external members who are no longer part of the group
        userExpenseRepository.deleteAll(toRemove);

        // Calculate total debtors (all users and external members except the creditor)
        int totalDebtors = getTotalDebtors(group, userCreditor, externalCreditor);
        if (totalDebtors == 0) {
            throw new IllegalArgumentException("There are no debtors to split the expense.");
        }

        // Calculate the amount each debtor should pay
        BigDecimal amountPerDebtor = BigDecimal.valueOf(expense.getTotalAmount())
                .divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP);

        // List to store all updated debts
        List<UserExpense> updatedExpenses = new ArrayList<>();

        // Process registered users (members of the group)
        for (User user : memberUsers) {
            // Skip the creditor if it's the same user
            if (userCreditor != null && user.getUserId().equals(userCreditor.getUserId())) continue;

            // Find existing debt or create a new one if it doesn't exist
            UserExpense userExpense = userExpenseRepository.findByExpenseAndDebtorUser(expense, user)
                    .orElseGet(UserExpense::new);

            userExpense.setExpense(expense);  // Set the expense for the debt
            userExpense.setDebtorUser(user);  // Set the user as the debtor
            userExpense.setAmount(amountPerDebtor.floatValue());  // Set the amount this user needs to pay

            // Set the creditor (either a user or an external member)
            if (userCreditor != null) {
                userExpense.setCreditorUser(userCreditor);
                userExpense.setCreditorExternalMember(null);
            } else {
                userExpense.setCreditorUser(null);
                userExpense.setCreditorExternalMember(externalCreditor);
            }

            updatedExpenses.add(userExpense);
        }

        // Process external members (those outside the registered user list)
        for (ExternalMember em : externalMembers) {
            // Skip the creditor if it's the same external member
            if (externalCreditor != null && em.getExternalMembersId().equals(externalCreditor.getExternalMembersId()))
                continue;

            // Find existing debt or create a new one if it doesn't exist
            UserExpense userExpense = userExpenseRepository.findByExpenseAndDebtorExternalMember(expense, em)
                    .orElseGet(UserExpense::new);

            userExpense.setExpense(expense);  // Set the expense for the debt
            userExpense.setDebtorExternalMember(em);  // Set the external member as the debtor
            userExpense.setAmount(amountPerDebtor.floatValue());  // Set the amount this external member needs to pay

            // Set the creditor (either a user or an external member)
            if (userCreditor != null) {
                userExpense.setCreditorUser(userCreditor);
                userExpense.setCreditorExternalMember(null);
            } else {
                userExpense.setCreditorUser(null);
                userExpense.setCreditorExternalMember(externalCreditor);
            }

            updatedExpenses.add(userExpense);
        }

        // Save all the updated debts to the repository
        userExpenseRepository.saveAll(updatedExpenses);
    }

    // Count total debtors (excluding the creditor)
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
}
