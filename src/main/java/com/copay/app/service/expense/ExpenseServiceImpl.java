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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    // Method triggered when updating registered users to update the expense.
    public void updateRegisteredUsers(Group group, User userCreditor, ExternalMember externalCreditor) {

        Expense expense = findExpenseByGroup(group);

        // Recalculates the individual amount of each user with new changes.
        updateExpenseDistribution(group, expense, userCreditor, externalCreditor);
    }

    @Transactional
    // Method triggered when updating external members to update the expense.
    public void updateExternalMembers(Group group, User userCreditor, ExternalMember externalCreditor) {

        // Reuse the same method as updateRegisteredUsers since the logic is identical.
        updateRegisteredUsers(group, userCreditor, externalCreditor);
    }

    @Transactional
    // Delete an expense of a specific group by retrieving expense id and group id.
    public void deleteExpenseByGroupAndId(Long groupId, Long expenseId) {

        Optional<Expense> expenseOptional = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // Delete associated UserExpense records
            userExpenseRepository.deleteByExpenseId(expense);

            // TODO: Delete payment confirmations related to the expense
            // paymentConfirmationRepository.deleteByExpense(expense);

            // Finally, delete the expense itself.
            expenseRepository.delete(expense);

        } else {
            throw new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId);
        }
    }

    //TODO: Implement this method of creating expenses when implementing more than one expense per group.
    @Override
    @Transactional
    public ExpenseResponseDTO createExpense(Long groupId, CreateExpenseRequestDTO request) {
        return null;
    }

    // Find the expenses of a group by id.
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpenses(Long groupId) {

        List<Expense> expenses = expenseRepository.findByGroupId_GroupId(groupId);

        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException("No expenses found for group " + groupId);
        }

        return expenses.stream()
                .map(this::mapToExpenseResponseDTO)
                .collect(Collectors.toList());
    }

    // Find an expense by id of a specific group by id.
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpense(Long groupId, Long expenseId) {
        Expense expense = expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with ID " + expenseId + " not found in group " + groupId));

        return mapToExpenseResponseDTO(expense);
    }

    @Override
    @Transactional
    public ConfirmPaymentResponseDTO confirmPayment(Long expenseId, Long userExpenseId) {
        // TODO
        return null;
    }

    // Sets the creditor (payer) for an expense.
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
        int totalDebtors = registeredMembers.size() + externalMembers.size() - 1;

        if (totalDebtors == 0) {
            throw new IllegalArgumentException("There are no debtors to split the expense.");
        }

        // Calculate amount per debtor.
        BigDecimal amountPerDebtor = BigDecimal.valueOf(expense.getTotalAmount())
                .divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP);

        List<UserExpense> updatedExpenses = new ArrayList<>();

        // Process registered users.
        for (User user : registeredMembers) {

            // Skip if this user is the creditor.
            if (userCreditor != null && user.getUserId().equals(userCreditor.getUserId())) {
                continue;
            }
            // Set the expense for the debt.
            UserExpense userExpense = new UserExpense();

            userExpense.setExpense(expense);
            userExpense.setDebtorUser(user);
            userExpense.setAmount(amountPerDebtor.floatValue());

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

        // Process external members.
        for (ExternalMember externalMember : externalMembers) {

            // Skip if this external member is the creditor.
            if (externalCreditor != null && externalMember.getExternalMembersId().equals(externalCreditor.getExternalMembersId())) {
                continue;
            }

            UserExpense userExpense = new UserExpense();
            userExpense.setExpense(expense);
            userExpense.setDebtorExternalMember(externalMember);
            userExpense.setAmount(amountPerDebtor.floatValue());

            if (userCreditor != null) {
                userExpense.setCreditorUser(userCreditor);
                userExpense.setCreditorExternalMember(null);
            } else {
                userExpense.setCreditorUser(null);
                userExpense.setCreditorExternalMember(externalCreditor);
            }

            updatedExpenses.add(userExpense);
        }

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
        for (UserExpense userExpense : userExpenses) {
            Float amount = userExpense.getAmount();
            Long creditorUserId = Optional.ofNullable(userExpense.getCreditorUser()).map(User::getUserId).orElse(null);
            Long creditorExternalMemberId = Optional.ofNullable(userExpense.getCreditorExternalMember()).map(ExternalMember::getExternalMembersId).orElse(null);

            // Create the DTO depending on whether it is a registered user or an external member
            DebtorResponseDTO dto = createDebtorResponseDTO(userExpense, creditorUserId, creditorExternalMemberId, amount);

            // Separate into registered members and external members
            if (userExpense.getDebtorUser() != null) {
                registeredMembers.add(dto);
            } else if (userExpense.getDebtorExternalMember() != null) {
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