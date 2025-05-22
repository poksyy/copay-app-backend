package com.copay.app.service.paymentconfirmation;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.paymentconfirmation.request.ConfirmPaymentRequestDTO;
import com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO;
import com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.PaymentConfirmation;
import com.copay.app.entity.relations.UserExpense;
import com.copay.app.exception.expense.ExpenseNotFoundException;
import com.copay.app.exception.paymentconfirmation.InvalidPaymentConfirmationException;
import com.copay.app.exception.paymentconfirmation.UnauthorizedPaymentConfirmationException;
import com.copay.app.exception.paymentconfirmation.UserExpenseNotFoundException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.repository.expense.UserExpenseRepository;
import com.copay.app.repository.GroupRepository;
import com.copay.app.repository.paymentconfirmation.PaymentConfirmationRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.expense.GroupExpenseService;
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


    public PaymentConfirmationServiceImpl(
            PaymentConfirmationRepository paymentConfirmationRepository,
            UserExpenseRepository userExpenseRepository,
            ExpenseRepository expenseRepository, JwtService jwtService, UserQueryService userQueryService, GroupQueryService groupQueryService) {
        this.paymentConfirmationRepository = paymentConfirmationRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.jwtService = jwtService;
        this.userQueryService = userQueryService;
        this.groupQueryService = groupQueryService;
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

        Group group = groupQueryService.getGroupById(groupId);

        groupQueryService.validateGroupCreator(group, token);

        return paymentConfirmationRepository.findAllUserExpensesByGroupId(groupId);
    }

    /**
     * Retrieves user expense IDs for the authenticated user within a group.
     *
     * @param groupId the ID of the group.
     * @param token the user's authentication token.
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
     * @param token the user's authentication token.
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
                paymentConfirmationRepository.findByUserExpense_UserExpenseId(request.getUserExpenseId())
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
     * @param token the user's authentication token.
     * @return the confirmed payment response DTO.
     */
    @Override
    @Transactional
    public PaymentResponseDTO confirmPayment(ConfirmPaymentRequestDTO request, String token) {

        // Get specific UserExpense (do not create or delete any).
        UserExpense userExpense = userExpenseRepository.findById(request.getUserExpenseId())
                .orElseThrow(() -> new UserExpenseNotFoundException(
                        "UserExpense with ID " + request.getUserExpenseId() + " not found"));

        // Get expense and group.
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        // Validates the group creator.
        groupQueryService.validateGroupCreator(group, token);

        Float confirmationAmount = request.getConfirmationAmount();
        Float currentDebt = userExpense.getAmount();

        // Validates that the amount to confirm is correct.
        confirmationAmount = getAmountToConfirm(confirmationAmount, currentDebt);

        // Check for an existing PaymentConfirmation.
        Optional<PaymentConfirmation> optionalConfirmation =
                paymentConfirmationRepository.findByUserExpense_UserExpenseId(userExpense.getUserExpenseId());

        // Initialized in null to avoid uninitialized variable error.
        PaymentConfirmation confirmation = null;

        // Case 1: No confirmation exists yet.
        if (optionalConfirmation.isEmpty()) {

            confirmation = new PaymentConfirmation();
            confirmation.setUserExpense(userExpense);
            confirmation.setConfirmationAmount(confirmationAmount);
            confirmation.setConfirmationDate(LocalDateTime.now());
            confirmation.setIsConfirmed(true);
            confirmation.setConfirmedAt(LocalDateTime.now());
        }

        // Case 2: A pending confirmation exists (isConfirmed == false).
        if (optionalConfirmation.isPresent() && !optionalConfirmation.get().getIsConfirmed()) {

            confirmation = optionalConfirmation.get();
            Float existingAmount = confirmation.getConfirmationAmount();

            // Validate combined payments do not exceed the debt.
            validateCombinedPaymentDoesNotExceedDebt(existingAmount, confirmationAmount, currentDebt);

            // If it's the same amount, sets on true the payment confirmation that was in isConfirmed == false.
            if (Objects.equals(existingAmount, confirmationAmount)) {

                // Confirm the existing one.
                confirmation.setIsConfirmed(true);
                confirmation.setConfirmedAt(LocalDateTime.now());
            }

            // If it's not same amount, creates a new payment confirmation.
            if (!Objects.equals(existingAmount, confirmationAmount)) {

                // Create a new confirmation.
                confirmation = new PaymentConfirmation();
                confirmation.setUserExpense(userExpense);
                confirmation.setConfirmationAmount(confirmationAmount);
                confirmation.setConfirmationDate(LocalDateTime.now());
                confirmation.setIsConfirmed(true);
                confirmation.setConfirmedAt(LocalDateTime.now());
            }
        }

        // Case 3: A confirmed payment already exists for that user_expense_id (isConfirmed == true).
        if (optionalConfirmation.isPresent() && optionalConfirmation.get().getIsConfirmed()) {

            // Still allow a new payment if there is remaining debt.
            confirmation = new PaymentConfirmation();
            confirmation.setUserExpense(userExpense);
            confirmation.setConfirmationAmount(confirmationAmount);
            confirmation.setConfirmationDate(LocalDateTime.now());
            confirmation.setIsConfirmed(true);
            confirmation.setConfirmedAt(LocalDateTime.now());
        }

        // Persist confirmation.
        paymentConfirmationRepository.save(confirmation);

        // Update debtor's remaining amount.
        userExpense.setAmount(currentDebt - confirmationAmount);
        userExpenseRepository.save(userExpense);

        // Update creditor's remaining balance.
        Float currentTotal = expense.getTotalAmount();
        expense.setTotalAmount(currentTotal - confirmationAmount);
        expenseRepository.save(expense);

        // Return confirmation response.
        return createResponseDTO(confirmation);
    }

    /**
     * Marks a pending payment confirmation as confirmed by the group creator.
     *
     * @param confirmationId the ID of the payment confirmation.
     * @param token the user's authentication token.
     * @return the updated payment response DTO.
     */
    @Override
    @Transactional
    public PaymentResponseDTO markPaymentAsConfirmed(Long confirmationId, String token) {

        // 1. Fetch pending confirmation
        PaymentConfirmation confirmation = paymentConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new InvalidPaymentConfirmationException("PaymentConfirmation not found"));

        // 2. Must be pending
        if (confirmation.getIsConfirmed()) {
            throw new InvalidPaymentConfirmationException("This payment is already confirmed.");
        }

        // 3. Load related entities
        UserExpense userExpense = confirmation.getUserExpense();
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        // 4. Only group creator can approve
        groupQueryService.validateGroupCreator(group, token);

        // 5. Confirm it
        confirmation.setIsConfirmed(true);
        confirmation.setConfirmedAt(LocalDateTime.now());
        paymentConfirmationRepository.save(confirmation);

        // 6. Reduce debtor’s remaining amount
        Float amount = confirmation.getConfirmationAmount();
        float newDebt = userExpense.getAmount() - amount;
        userExpense.setAmount(newDebt);
        userExpenseRepository.save(userExpense);

        // 7. Reduce creditor’s total
        Float expenseTotal = expense.getTotalAmount() - amount;
        expense.setTotalAmount(expenseTotal);
        expenseRepository.save(expense);

        return createResponseDTO(confirmation);
    }

    /**
     * Deletes a payment confirmation by ID, only allowed by the group creator.
     *
     * @param confirmationId the ID of the payment confirmation.
     * @param token the user's authentication token.
     * @return a message response DTO confirming deletion.
     */
    @Override
    @Transactional
    public MessageResponseDTO deletePaymentConfirmation(Long confirmationId, String token) {

        PaymentConfirmation confirmation = paymentConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new EntityNotFoundException("Payment confirmation not found with id " + confirmationId));

        UserExpense userExpense = confirmation.getUserExpense();
        Expense expense = userExpense.getExpense();
        Group group = expense.getGroupId();

        groupQueryService.validateGroupCreator(group, token);

        paymentConfirmationRepository.delete(confirmation);

        return new MessageResponseDTO("Payment confirmation deleted successfully");

    }

    // TODO: Make sure this is used when creating a payment confirmation instance.
    private PaymentConfirmation createNewConfirmation(UserExpense userExpense, Float amount) {
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
        return new PaymentResponseDTO(
                confirmation.getPaymentConfirmationId(),
                confirmation.getUserExpense().getUserExpenseId(),
                confirmation.getConfirmationAmount(),
                confirmation.getConfirmationDate(),
                confirmation.getIsConfirmed(),
                confirmation.getConfirmedAt()
        );
    }
}
