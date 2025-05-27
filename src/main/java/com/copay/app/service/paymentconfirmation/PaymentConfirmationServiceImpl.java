package com.copay.app.service.paymentconfirmation;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.paymentconfirmation.request.ConfirmPaymentRequestDTO;
import com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO;
import com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.PaymentConfirmation;
import com.copay.app.entity.relations.UserExpense;
import com.copay.app.exception.expense.ExpenseNotFoundException;
import com.copay.app.exception.paymentconfirmation.InvalidPaymentConfirmationException;
import com.copay.app.exception.paymentconfirmation.UnauthorizedPaymentConfirmationException;
import com.copay.app.exception.paymentconfirmation.UserExpenseNotFoundException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.repository.expense.UserExpenseRepository;
import com.copay.app.repository.paymentconfirmation.PaymentConfirmationRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.notification.NotificationService;
import com.copay.app.service.query.GroupQueryService;
import com.copay.app.service.query.UserQueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentConfirmationServiceImpl implements PaymentConfirmationService {

    private final PaymentConfirmationRepository paymentConfirmationRepository;

    private final UserExpenseRepository userExpenseRepository;

    private final ExpenseRepository expenseRepository;

    private final JwtService jwtService;

    private final UserQueryService userQueryService;

    private final GroupQueryService groupQueryService;

    private final NotificationService notificationService;


    public PaymentConfirmationServiceImpl(
            PaymentConfirmationRepository paymentConfirmationRepository,
            UserExpenseRepository userExpenseRepository,
            ExpenseRepository expenseRepository, JwtService jwtService, UserQueryService userQueryService,
        this.paymentConfirmationRepository = paymentConfirmationRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.jwtService = jwtService;
        this.userQueryService = userQueryService;
        this.groupQueryService = groupQueryService;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves all user expenses by group ID.
     *
     * @param groupId the ID of the group.
     * @param token the user's authentication token.
     * @return a list of payment responses for the specified group.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllUserExpensesByGroupId(Long groupId, String token) {

        // Find a group via GroupQueryService, which delegates exception handling to GroupValidator.
        groupQueryService.getGroupById(groupId);

        return paymentConfirmationRepository.findAllUserExpensesByGroupId(groupId);
    }

    /**
     * Retrieves user expense IDs for the authenticated user within a group.
     *
     * @param groupId the ID of the group.
     * @return a list of payment responses for the user's expenses in the group.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getUserExpenseIdsForUserInGroup(Long groupId, String token) {

        // Extract the phone number thanks to the token identifier.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        User user = userQueryService.getUserByPhone(phoneNumber);

        groupQueryService.getGroupById(groupId);

        return paymentConfirmationRepository.findUserExpenseIdsByGroupIdAndUserId(groupId, user.getUserId());
    }

    /**
     * Retrieves all unconfirmed payment confirmations for a group.
     *
     * @param groupId the ID of the group.
     * @return a list of unconfirmed payment confirmation responses.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ListUnconfirmedPaymentConfirmationResponseDTO> getAllUnconfirmedConfirmations(Long groupId) {

        return paymentConfirmationRepository.findUnconfirmedByGroupId(groupId);
    }

    /**
     * Requests a payment confirmation for a user expense.
     *
     * @param request the payment confirmation request DTO.
     * @return the created payment response DTO.
     */
    @Override
    @Transactional
    public PaymentResponseDTO requestPayment(ConfirmPaymentRequestDTO request, String token) {

        String userPhone = jwtService.getUserIdentifierFromToken(token);

        User currentUser = userQueryService.getUserByPhone(userPhone);

        // Retrieve and validate the UserExpense.
        UserExpense userExpense = validateUserExpenseExists(request.getUserExpenseId());

        // Validate that the currentUser is the debtor.
        validateUserIsDebtor(userExpense, currentUser);

        // Validate that there is no existing pending unconfirmed payment for this UserExpense.
        Optional<PaymentConfirmation> existingPending =
                paymentConfirmationRepository.findFirstByUserExpense_UserExpenseIdAndIsConfirmedFalse(request.getUserExpenseId())
                        .filter(pc -> !pc.getIsConfirmed());

        if (existingPending.isPresent()) {
            throw new InvalidPaymentConfirmationException("You already have a pending payment request for this expense.");
        }

        float amountToConfirm = getAmountToConfirm(request.getConfirmationAmount(), userExpense.getAmount());

        PaymentConfirmation newConfirmation = new PaymentConfirmation();
        newConfirmation.setUserExpense(userExpense);
        newConfirmation.setConfirmationAmount(amountToConfirm);
        newConfirmation.setConfirmationDate(LocalDateTime.now());
        // isConfirmed is false because this payment confirmation needs to be validated by the creator.
        newConfirmation.setIsConfirmed(false);

        paymentConfirmationRepository.save(newConfirmation);

        return createResponseDTO(newConfirmation);
    }

    /**
     * Confirms a payment for a user expense.
     *
     * @param request the payment confirmation request DTO.
     * @return the confirmed payment response DTO.
     */
    @Override
    @Transactional
    public PaymentResponseDTO confirmPayment(ConfirmPaymentRequestDTO request, String token) {

        // Get UserExpense by ID.
        UserExpense userExpense = userExpenseRepository.findById(request.getUserExpenseId())
                .orElseThrow(() -> new UserExpenseNotFoundException(
                        "UserExpense with ID " + request.getUserExpenseId() + " not found"));

        // Get related Expense and Group.
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        // Validate group creator.
        groupQueryService.validateGroupCreator(group, token);

        Float confirmationAmount = request.getConfirmationAmount();
        Float currentDebt = userExpense.getAmount();

        // Validate requested amount does not exceed the current debt.
        confirmationAmount = getAmountToConfirm(confirmationAmount, currentDebt);

        // Get all pending PaymentConfirmations for the UserExpense.
        List<PaymentConfirmation> pendingConfirmations =
                paymentConfirmationRepository.findAllByUserExpense_UserExpenseIdAndIsConfirmed(
                        userExpense.getUserExpenseId(), false);

        // No existing confirmations for that user_expense_id.
        if (pendingConfirmations.isEmpty()) {

            // Create and confirm a new PaymentConfirmation
            return createAndConfirmNewPayment(confirmationAmount, userExpense, expense, group);
        }

        // There is at least one existing pending confirmation for that user_expense_id.
        else {
            // Check if one of the existing confirmations matches the confirmation amount
            for (PaymentConfirmation pending : pendingConfirmations) {

                // If the existing confirmation matches the confirmation amount, confirm it.
                if (Objects.equals(pending.getConfirmationAmount(), confirmationAmount)) {

                    return markPaymentAsConfirmed(pending.getPaymentConfirmationId(), token);
                }
            }

            // Validates if the new confirmation amount does not exceed the current debt.
            Float totalPendingAmount = (float) pendingConfirmations.stream()
                    .mapToDouble(PaymentConfirmation::getConfirmationAmount)
                    .sum();

            // Check that the combined amount (pending confirmation and new confirmation) does not exceed the debt.
            if (confirmationAmount + totalPendingAmount > currentDebt) {

                throw new InvalidPaymentConfirmationException(
                        "The total payment amount exceeds the current debt.");
            }

            // Create and confirm a new PaymentConfirmation for the additional amount.
            return createAndConfirmNewPayment(confirmationAmount, userExpense, expense, group);
        }
    }

    // Helper method to create and confirm a new PaymentConfirmation.
    private PaymentResponseDTO createAndConfirmNewPayment(
            Float confirmationAmount,
            UserExpense userExpense,
            Expense expense,
            Group group) {

        // Create the new PaymentConfirmation.
        PaymentConfirmation confirmation = new PaymentConfirmation();
        confirmation.setUserExpense(userExpense);
        confirmation.setConfirmationAmount(confirmationAmount);
        confirmation.setConfirmationDate(LocalDateTime.now());
        confirmation.setIsConfirmed(true);
        confirmation.setConfirmedAt(LocalDateTime.now());

        // Persists in the database.
        paymentConfirmationRepository.save(confirmation);

        // Update user debt.
        Float currentDebt = userExpense.getAmount();
        userExpense.setAmount(currentDebt - confirmationAmount);
        userExpenseRepository.save(userExpense);

        // Update total amount in expense.
        Float currentTotal = expense.getTotalAmount();
        expense.setTotalAmount(currentTotal - confirmationAmount);
        expenseRepository.save(expense);

        // Notify debtor that the payment has been confirmed (only if it's a regular user, not an external member)
        User debtorUser = userExpense.getDebtorUser();
        if (debtorUser != null) {
            String notificationMessage = String.format(
                    "Your payment of %.2f in group '%s' has been confirmed.",
                    confirmationAmount,
                    group.getName()
            );
            notificationService.createNotification(debtorUser, notificationMessage);
        } else {
            // External member - no notification is sent as they don't have user accounts
            ExternalMember debtorExternalMember = userExpense.getDebtorExternalMember();
            // Log the confirmation for external member (optional)
            System.out.println("Payment confirmed for external member: " +
                    (debtorExternalMember != null ? debtorExternalMember.getName() : "Unknown") +
                    " - Amount: " + confirmationAmount);
        }

        // Return the response DTO.
        return createResponseDTO(confirmation);
    }

    /**
     * Marks a pending payment confirmation as confirmed by the group creator.
     *
     * @param confirmationId the ID of the payment confirmation.
     * @param token          the user's authentication token.
     * @return the updated payment response DTO.
     */
    @Override
    @Transactional
    public PaymentResponseDTO markPaymentAsConfirmed(Long confirmationId, String token) {

        // Validates that the payment confirmation exists.
        PaymentConfirmation confirmation = paymentConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new InvalidPaymentConfirmationException("Payment confirmation with ID " + confirmationId + " not found"));

        // Validation that the confirmation is not already confirmed.
        if (confirmation.getIsConfirmed()) {
            throw new InvalidPaymentConfirmationException("Payment confirmation with ID " + confirmationId + " is already confirmed.");
        }

        // Load related entities with payment confirmation.
        UserExpense userExpense = confirmation.getUserExpense();
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        // Validates if the user is the group creator.
        groupQueryService.validateGroupCreator(group, token);

        // Confirm the payment confirmation.
        confirmation.setIsConfirmed(true);
        confirmation.setConfirmedAt(LocalDateTime.now());
        paymentConfirmationRepository.save(confirmation);

        // Reduce the debtor's remaining debt.
        Float amount = confirmation.getConfirmationAmount();
        float newDebt = userExpense.getAmount() - amount;
        userExpense.setAmount(newDebt);
        userExpenseRepository.save(userExpense);

        // Reduce creditor’s total amount in expense.
        Float expenseTotal = expense.getTotalAmount() - amount;
        expense.setTotalAmount(expenseTotal);
        expenseRepository.save(expense);

        // Send notification to debtor user (only if it's a regular user, not an external member)
        User debtorUser = userExpense.getDebtorUser();
        if (debtorUser != null) {
            String notificationMessage = String.format(
                    "Your payment of %.2f in group '%s' has been confirmed.",
                    amount,
                    group.getName()
            );
            notificationService.createNotification(debtorUser, notificationMessage);
        } else {
            // External member - no notification is sent as they don't have user accounts
            ExternalMember debtorExternalMember = userExpense.getDebtorExternalMember();
            // Log the confirmation for external member (optional)
            System.out.println("Payment confirmed for external member: " +
                    (debtorExternalMember != null ? debtorExternalMember.getName() : "Unknown") +
                    " - Amount: " + amount);
        }

        return createResponseDTO(confirmation);
    }

    /**
     * Deletes a payment confirmation by ID, only allowed by the group creator.
     *
     * @param confirmationId the ID of the payment confirmation.
     * @return a message response DTO confirming deletion.
     */
    @Override
    @Transactional
    public MessageResponseDTO deletePaymentConfirmation(Long confirmationId, String token) {

        // Validates that the payment confirmation exists.
        PaymentConfirmation confirmation = paymentConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new InvalidPaymentConfirmationException("Payment confirmation with ID " + confirmationId + " not found"));

        UserExpense userExpense = confirmation.getUserExpense();
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        groupQueryService.validateGroupCreator(group, token);

        paymentConfirmationRepository.delete(confirmation);

        return new MessageResponseDTO("Payment confirmation deleted successfully");

    }

    // TODO: Make sure this is used when creating a payment confirmation instance.
    private PaymentConfirmation createPaymentConfirmation(UserExpense userExpense, Float amount) {

        PaymentConfirmation confirmation = new PaymentConfirmation();
        confirmation.setUserExpense(userExpense);
        confirmation.setConfirmationAmount(amount);
        confirmation.setConfirmationDate(LocalDateTime.now());
        confirmation.setIsConfirmed(true);
        confirmation.setConfirmedAt(LocalDateTime.now());
        return confirmation;
    }

    private static float getAmountToConfirm(float amountToConfirm, float amountDue) {

        // Can't pay more than the owed money.
        if (amountToConfirm > amountDue) {
            throw new InvalidPaymentConfirmationException("The confirmation amount exceeds the amount owed.");
        }

        // TODO: This custom exception is never triggered since the same validation is present in the request DTO.
        // Can't pay negative.
        if (amountToConfirm <= 0f) {
            throw new InvalidPaymentConfirmationException("The confirmation amount must be greater than zero.");
        }
        return amountToConfirm;
    }

    // Helper methods.
    private Expense validateExpenseExists(Long expenseId, Long groupId) {

        return expenseRepository.findByExpenseIdAndGroupId_GroupId(expenseId, groupId)
                .orElseThrow(() -> new ExpenseNotFoundException(
                        "Expense with ID " + expenseId + " not found in group " + groupId));
    }

    private UserExpense validateUserExpenseExists(Long userExpenseId) {
        return userExpenseRepository.findById(userExpenseId)
                .orElseThrow(() -> new InvalidPaymentConfirmationException("User expense with ID " + userExpenseId + " not found"));
    }


    private void validateUserIsDebtor(UserExpense userExpense, User currentUser) {

        if (userExpense.getDebtorUser() == null ||
                !userExpense.getDebtorUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedPaymentConfirmationException(
                    "User is not authorized to create a payment confirmation for this expense");
        }
    }

    private void validateCombinedPaymentDoesNotExceedDebt(float existingAmount, float newAmount, float currentDebt) {

        if (existingAmount + newAmount > currentDebt) {
            throw new InvalidPaymentConfirmationException("Combined payments exceed the remaining debt.");
        }
    }

    private PaymentResponseDTO createResponseDTO(PaymentConfirmation confirmation) {
        UserExpense userExpense = confirmation.getUserExpense();

        String debtorUsername = userExpense.getDebtorUser() != null
                ? userExpense.getDebtorUser().getUsername()
                : userExpense.getDebtorExternalMember() != null
                ? userExpense.getDebtorExternalMember().getName()
                : "Unknown";

        String creditorUsername = userExpense.getCreditorUser() != null
                ? userExpense.getCreditorUser().getUsername()
                : userExpense.getCreditorExternalMember() != null
                ? userExpense.getCreditorExternalMember().getName()
                : "Unknown";

        return new PaymentResponseDTO(
                confirmation.getPaymentConfirmationId(),
                userExpense.getUserExpenseId(),
                confirmation.getConfirmationAmount(),
                confirmation.getConfirmationDate(),
                confirmation.getIsConfirmed(),
                confirmation.getConfirmedAt(),
                debtorUsername,
                creditorUsername
        );
    }
}
