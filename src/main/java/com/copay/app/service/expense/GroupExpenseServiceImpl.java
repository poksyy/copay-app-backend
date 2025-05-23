    package com.copay.app.service.expense;
    
    import com.copay.app.entity.Expense;
    import com.copay.app.entity.Group;
    import com.copay.app.entity.User;
    import com.copay.app.entity.relations.ExternalMember;
    import com.copay.app.entity.relations.UserExpense;
    import com.copay.app.exception.expense.DebtorNotFoundException;
    import com.copay.app.exception.expense.ExpenseNotFoundException;
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
    
        private final UserQueryService userQueryService;
    
        public GroupExpenseServiceImpl(ExpenseRepository expenseRepository, UserExpenseRepository userExpenseRepository, UserQueryService userQueryService) {
    
            this.expenseRepository = expenseRepository;
            this.userExpenseRepository = userExpenseRepository;
            this.userQueryService = userQueryService;
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
            updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
        }
    
        @Override
        @Transactional
        // Method triggered when updating the group price to update the expenses.
        public void updateExpenseTotalAmount(Group group, Float newPrice) {
    
            // Get existing expense and update its price and payer.
            Expense expense = findExpenseByGroup(group);
            expense.setTotalAmount(newPrice);
    
            // One of the creditors is going to be null, since only 1 creditor can be available
            User userCreditor = expense.getPaidByUser();
            ExternalMember externalCreditor = expense.getPaidByExternalMember();
    
            // Recalculates the individual amount of each user with new changes.
            updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    
            expenseRepository.save(expense);
        }
    
        @Override
        @Transactional
        // Method triggered when updating members through GroupServiceImpl.
        public void updateExpenseGroupMembers(Group group, User userCreditor, ExternalMember externalCreditor) {
    
            Expense expense = findExpenseByGroup(group);
    
            // Recalculates the individual amount of each user with new changes.
            updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
        }
    
    
        // Updates the distribution after expenses changes.
        private void updateExpenseDistribution(Group group, Expense expense, User userCreditor, ExternalMember externalCreditor) {
    
            // Get all registeredMembers (including the creditor if they're a registered member).
            List<User> registeredMembers = new ArrayList<>(group.getRegisteredMembers().stream()
                    .map(gm -> gm.getId().getUser())
                    .toList());
    
            // Store the group creator.
            User creator = group.getCreatedBy();
    
            // Ensure that the creator is being added to the expense distribution if he's not the payer.
            if (registeredMembers.stream().noneMatch(u -> u.getUserId().equals(creator.getUserId()))) {
                registeredMembers.add(creator);
            }
    
            // Get all external members (including the creditor if they're an external member).
            List<ExternalMember> externalMembers = group.getExternalMembers().stream()
                    .toList();
    
            // Remove existing debts.
            List<UserExpense> existingExpenses = userExpenseRepository.findByExpenseId(expense);
    
            userExpenseRepository.deleteAll(existingExpenses);
    
            // Calculate total debtors (all members except the creditor).
            int totalDebtors = registeredMembers.size() + externalMembers.size();
    
            if (totalDebtors == 0) {
                throw new DebtorNotFoundException("There are no debtors for expense with ID " + expense.getGroupId());
            }
    
            // Calculate amount per debtor.
            BigDecimal amountPerMember = BigDecimal.valueOf(expense.getTotalAmount())
                    .divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP);
    
            List<UserExpense> updatedExpenses = new ArrayList<>();
    
            // Process registered users.
            for (User user : registeredMembers) {
    
                // Set the expense for the debt.
                UserExpense userExpense = new UserExpense();
    
                userExpense.setExpense(expense);
                userExpense.setDebtorUser(user);
    
                userExpense.setAmount(amountPerMember.floatValue());
    
                // Set the creditor (either a user or an external member).
                setUserExpenseCreditor(userExpense, userCreditor, externalCreditor);
    
                updatedExpenses.add(userExpense);
            }
    
            // Process external members.
            for (ExternalMember externalMember : externalMembers) {
    
                UserExpense userExpense = new UserExpense();
    
                userExpense.setExpense(expense);
                userExpense.setDebtorExternalMember(externalMember);
                userExpense.setAmount(amountPerMember.floatValue());
    
                // Set the creditor (either a user or an external member).
                setUserExpenseCreditor(userExpense, userCreditor, externalCreditor);
    
                updatedExpenses.add(userExpense);
            }
    
            userExpenseRepository.saveAll(updatedExpenses);
        }
    
        // Finds an expense by group or throws an exception.
        private Expense findExpenseByGroup(Group group) {
    
            return expenseRepository.findByGroupId(group)
                    .orElseThrow(() -> new ExpenseNotFoundException("Expense of the group " + group.getGroupId() + " not found"));
        }
    
        // Sets the creditor (payer) for a general expense.
        // This method is used to assign the creditor to an Expense object (general expense).
        private void setCreditor(Expense expense, User userCreditor, ExternalMember externalCreditor) {
    
            if (userCreditor != null) {
    
                expense.setPaidByUser(userCreditor);
    
            } else if (externalCreditor != null) {
                expense.setPaidByExternalMember(externalCreditor);
            }
        }
    
        // Sets the creditor (payer) for a user-specific expense.
        // This method is used to assign the creditor to a UserExpense object (individual user’s share of the expense).
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