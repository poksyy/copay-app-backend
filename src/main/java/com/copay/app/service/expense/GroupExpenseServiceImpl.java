package com.copay.app.service.expense;

import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;
import com.copay.app.exception.expense.DebtorNotFoundException;
import com.copay.app.exception.expense.ExpenseNotFoundException;
import com.copay.app.exception.group.ExternalMemberNotFoundException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.repository.expense.UserExpenseRepository;
import com.copay.app.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer that handles expense-related logic triggered from GroupServiceImpl.
 * These methods are exclusively used when managing group-related operations
 * such as creating a group, updating its price or members, and need to initialize
 * or modify corresponding expenses.
 */
@Service
public class GroupExpenseServiceImpl implements GroupExpenseService{

    private final ExpenseRepository expenseRepository;

    private final UserExpenseRepository userExpenseRepository;

    public GroupExpenseServiceImpl(ExpenseRepository expenseRepository, UserExpenseRepository userExpenseRepository) {

        this.expenseRepository = expenseRepository;
        this.userExpenseRepository = userExpenseRepository;
    }

    @Override
    @Transactional
    // Method triggered when creating a new group to initialize the expense management.
    public void initializeExpenseFromGroup(Group group, Float estimatedPrice, User userCreditor, ExternalMember externalCreditor) {

        // Create and save the main expense.
        Expense expense = new Expense();

        expense.setGroupId(group);
        expense.setTotalAmount(estimatedPrice);

        setCreditor(expense, userCreditor, externalCreditor);

        expenseRepository.save(expense);

        // Calculate and save all individual expenses.
        createExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    @Override
    @Transactional
    // Method triggered when updating the group price to update the expenses.
    public void updateExpenseTotalAmount(Group group, Float newPrice) {

        // Get existing expense and update its price and payer.
        Expense expense = findExpenseByGroup(group);
        expense.setTotalAmount(newPrice);

        // Update the numbers of existing user expenses without creating new ones.
        adjustExpenseAmounts(group, expense);

        expenseRepository.save(expense);
    }

    // Method triggered when updating members through GroupServiceImpl.
    public void updateExpenseGroupMembers(Group group, User userCreditor, ExternalMember externalCreditor) {

        // Find the existing expense for this group
        Expense expense = findExpenseByGroup(group);

        // Update expenses based on member changes
        adjustExpenseMembers(group, expense, userCreditor, externalCreditor);
    }

    // Updates the distribution after expenses changes.
    private void createExpenseDistribution(Group group, Expense expense, User userCreditor, ExternalMember externalCreditor) {

        List<User> users = collectRegisteredUsers(group);
        List<ExternalMember> externals = collectExternalMembers(group);
        BigDecimal perMember = calculatePerMember(expense.getTotalAmount(), users.size() + externals.size());

        // Remove existing debts.
        userExpenseRepository.deleteAll(userExpenseRepository.findByExpenseId(expense));

        List<UserExpense> updatedExpenses = new ArrayList<>();

        // Process registered users.
        for (User user : users) {
            UserExpense userExpense = new UserExpense();
            userExpense.setExpense(expense);
            userExpense.setDebtorUser(user);
            userExpense.setAmount(perMember.floatValue());

            // Set the creditor (either a user or an external member).
            setUserExpenseCreditor(userExpense, userCreditor, externalCreditor);

            updatedExpenses.add(userExpense);
        }

        // Process external members.
        for (ExternalMember externalMember : externals) {
            UserExpense userExpense = new UserExpense();
            userExpense.setExpense(expense);
            userExpense.setDebtorExternalMember(externalMember);
            userExpense.setAmount(perMember.floatValue());

            // Set the creditor (either a user or an external member).
            setUserExpenseCreditor(userExpense, userCreditor, externalCreditor);

            updatedExpenses.add(userExpense);
        }

        userExpenseRepository.saveAll(updatedExpenses);
    }

    // Updates only the numbers of existing user expenses without creating new ones.
    private void adjustExpenseAmounts(Group group, Expense expense) {

        // Store registered members.
        List<User> users = collectRegisteredUsers(group);

        // Store external members.
        List<ExternalMember> externals = collectExternalMembers(group);

        BigDecimal perMember = calculatePerMember(expense.getTotalAmount(), users.size() + externals.size());

        // Get existing user expenses.
        List<UserExpense> existingExpenses = userExpenseRepository.findByExpenseId(expense);

        // Update amounts for existing user expenses.
        for (UserExpense userExpense : existingExpenses) {
            userExpense.setAmount(perMember.floatValue());
        }

        userExpenseRepository.saveAll(existingExpenses);
    }

    // Updates expenses based on member changes.
    private void adjustExpenseMembers(Group group, Expense expense, User userCreditor, ExternalMember externalCreditor) {

        // Store registered members.
        List<User> users = collectRegisteredUsers(group);

        // Store external members and ensure they are persisted.
        List<ExternalMember> externals = collectExternalMembers(group);

        // Calculate the amount per member based on total participants.
        BigDecimal perMember = calculatePerMember(expense.getTotalAmount(), users.size() + externals.size());

        // Get all existing user expenses for this expense.
        List<UserExpense> existingExpenses = userExpenseRepository.findByExpenseId(expense);

        // Categorize existing expenses.
        List<UserExpense> expensesToKeep = new ArrayList<>();
        List<UserExpense> expensesToDelete = new ArrayList<>();
        List<UserExpense> expensesToCreate = new ArrayList<>();

        // Identify expenses to keep and delete based on current members.
        for (UserExpense userExpense : existingExpenses) {

            // Check registered user expenses.
            if (userExpense.getDebtorUser() != null) {

                boolean stillMember = users.stream()
                        .anyMatch(u -> u.getUserId().equals(userExpense.getDebtorUser().getUserId()));

                if (stillMember) {
                    expensesToKeep.add(userExpense);
                } else {
                    expensesToDelete.add(userExpense);
                }

                // Check external member expenses.
            } else if (userExpense.getDebtorExternalMember() != null) {

                boolean stillMember = externals.stream()
                        .anyMatch(em -> em.getExternalMembersId().equals(userExpense.getDebtorExternalMember().getExternalMembersId()));

                if (stillMember) {
                    expensesToKeep.add(userExpense);

                } else {
                    expensesToDelete.add(userExpense);
                }
            }
        }

        // Create expenses for new registered members.
        for (User user : users) {

            boolean hasExpense = expensesToKeep.stream()
                    .anyMatch(ue -> ue.getDebtorUser() != null && ue.getDebtorUser().getUserId().equals(user.getUserId()));

            if (!hasExpense) {

                // Creates user_expenses_id for the new registered members added.
                UserExpense newUserExpense = new UserExpense();
                newUserExpense.setExpense(expense);
                newUserExpense.setDebtorUser(user);
                setUserExpenseCreditor(newUserExpense, userCreditor, externalCreditor);
                newUserExpense.setAmount(perMember.floatValue());
                expensesToCreate.add(newUserExpense);
            }
        }

        // Create expenses for new external members.
        for (ExternalMember externalMember : externals) {

            boolean hasExpense = expensesToKeep.stream()
                    .anyMatch(ue -> ue.getDebtorExternalMember() != null &&
                            ue.getDebtorExternalMember().getExternalMembersId().equals(externalMember.getExternalMembersId()));

            if (!hasExpense) {
                
                // Creates user_expenses_id for the new external members added.
                UserExpense newUserExpense = new UserExpense();
                newUserExpense.setExpense(expense);
                newUserExpense.setDebtorExternalMember(externalMember);
                setUserExpenseCreditor(newUserExpense, userCreditor, externalCreditor);
                newUserExpense.setAmount(perMember.floatValue());
                expensesToCreate.add(newUserExpense);
            }
        }

        // Delete expenses for removed members.
        userExpenseRepository.deleteAll(expensesToDelete);

        // Update amounts for existing user expenses.
        for (UserExpense userExpense : expensesToKeep) {
            userExpense.setAmount(perMember.floatValue());
        }

        // Save all expenses.
        userExpenseRepository.saveAll(expensesToKeep);
        userExpenseRepository.saveAll(expensesToCreate);
    }

    // Finds an expense by group or throws an exception.
    private Expense findExpenseByGroup(Group group) {
        return expenseRepository.findByGroupId(group)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense of the group " + group.getGroupId() + " not found"));
    }

    // Collects registered users including creator
    private List<User> collectRegisteredUsers(Group group) {

        List<User> registeredMembers = new ArrayList<>(group.getRegisteredMembers().stream()
                .map(gm -> gm.getId().getUser()).toList());
        User creator = group.getCreatedBy();

        if (registeredMembers.stream().noneMatch(u -> u.getUserId().equals(creator.getUserId()))) {
            registeredMembers.add(creator);
        }
        return registeredMembers;
    }


    // Collects external members and validates they are persisted.
    private List<ExternalMember> collectExternalMembers(Group group) {

        List<ExternalMember> externals = new ArrayList<>(group.getExternalMembers());

        // Verify all external members have been persisted (have ID)
        for (int i = 0; i < externals.size(); i++) {
            ExternalMember em = externals.get(i);
            System.err.println("ExternalMember[" + i + "]: ID=" + em.getExternalMembersId() + ", Name=" + em.getName());

            if (em.getExternalMembersId() == null) {

                throw new ExternalMemberNotFoundException("ExternalMember without ID found: " + em.getName());
            }
        }

        return externals;
    }

    // Calculates amount per member and validates debtors
    private BigDecimal calculatePerMember(Float totalAmount, int totalDebtors) {

        if (totalDebtors == 0) {
            throw new DebtorNotFoundException("There are no debtors for expense with total " + totalAmount);
        }

        return BigDecimal.valueOf(totalAmount)
                .divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP);
    }

    // Sets the creditor (payer) for a general expense.
    private void setCreditor(Expense expense, User userCreditor, ExternalMember externalCreditor) {

        if (userCreditor != null) {
            expense.setPaidByUser(userCreditor);

        } else if (externalCreditor != null) {
            expense.setPaidByExternalMember(externalCreditor);
        }
    }

    // Sets the creditor (payer) for a user-specific expense.
    private void setUserExpenseCreditor(UserExpense userExpense, User userCreditor, ExternalMember externalCreditor) {

        if (userCreditor != null) {

            userExpense.setCreditorUser(userCreditor);
            userExpense.setCreditorExternalMember(null);

        } else if (externalCreditor != null) {
            userExpense.setCreditorUser(null);
            userExpense.setCreditorExternalMember(externalCreditor);
        }
    }
}
