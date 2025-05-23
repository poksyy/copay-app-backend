package com.copay.app.controller;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.GetGroupRequestDTO;
import com.copay.app.dto.paymentconfirmation.request.ConfirmPaymentRequestDTO;
import com.copay.app.dto.paymentconfirmation.request.DeletePaymentConfirmationRequestDTO;
import com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO;
import com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO;
import com.copay.app.service.paymentconfirmation.PaymentConfirmationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-confirmations")
public class PaymentConfirmationController {

    private final PaymentConfirmationService paymentConfirmationService;


    public PaymentConfirmationController(PaymentConfirmationService paymentConfirmationService) {

        this.paymentConfirmationService = paymentConfirmationService;
    }

    /**
     * Retrieves all user expense IDs for a specific group.
     *
     * @param groupId the ID of the group.
     * @return a list of user expense IDs within the specified group.
     */
    @GetMapping("/groups/{groupId}/user-expenses/ids")
    public ResponseEntity<List<PaymentResponseDTO>> getUserExpenseIds(@PathVariable Long groupId,
                                                                      @Valid @ModelAttribute GetGroupRequestDTO getGroupRequestDTO) {

        // The groupId is manually added to the DTO for validation.
        getGroupRequestDTO.setGroupId(groupId);

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        List<PaymentResponseDTO> ids = paymentConfirmationService.getUserExpenseIdsForUserInGroup(groupId, token);

        return ResponseEntity.ok(ids);
    }

    /**
     * Retrieves all user expenses by group ID.
     *
     * @param groupId the ID of the group.
     *
     * @return a list of user expenses for the specified group.
     */
    @GetMapping("/groups/{groupId}/user-expenses")
    public ResponseEntity<List<PaymentResponseDTO>> getAllUserExpensesByGroup(@PathVariable Long groupId,
                                                                              @Valid @ModelAttribute GetGroupRequestDTO getGroupRequestDTO) {

        // The groupId is manually added to the DTO for validation.
        getGroupRequestDTO.setGroupId(groupId);

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        List<PaymentResponseDTO> userExpenses = paymentConfirmationService.getAllUserExpensesByGroupId(groupId, token);
        return ResponseEntity.ok(userExpenses);
    }

    /**
     * Retrieves a list of all unconfirmed payment confirmations for a specific group.
     *
     * @param groupId the ID of the group.
     * @return a list of unconfirmed payment confirmations.
     */
    @GetMapping("/groups/{groupId}/unconfirmed")
    public ResponseEntity<List<ListUnconfirmedPaymentConfirmationResponseDTO>> getUnconfirmedConfirmations(@PathVariable Long groupId,
                                                                                                           @Valid @ModelAttribute GetGroupRequestDTO getGroupRequestDTO) {

        // The groupId is manually added to the DTO for validation.
        getGroupRequestDTO.setGroupId(groupId);

        List<ListUnconfirmedPaymentConfirmationResponseDTO> unconfirmedPayments = paymentConfirmationService.getAllUnconfirmedConfirmations(groupId);

        return ResponseEntity.ok(unconfirmedPayments);
    }

    /**
     * Allows a user to request a payment confirmation.
     *
     * @param confirmPaymentRequestDTO the details of the payment confirmation request.
     * @return the payment confirmation response containing relevant details.
     */
    @PostMapping("/request-payment")
    public ResponseEntity<?> requestPaymentConfirmation(@RequestBody @Valid ConfirmPaymentRequestDTO confirmPaymentRequestDTO) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        PaymentResponseDTO confirmPaymentResponseDTO = paymentConfirmationService.requestPayment(confirmPaymentRequestDTO, token);

        return ResponseEntity.ok(confirmPaymentResponseDTO);
    }

    /**
     * Endpoint for a group creator to confirm a payment.
     *
     * @param confirmPaymentRequestDTO the details of the payment confirmation.
     * @return the payment confirmation response containing relevant details.
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody @Valid ConfirmPaymentRequestDTO confirmPaymentRequestDTO) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        PaymentResponseDTO confirmPaymentResponseDTO = paymentConfirmationService.confirmPayment(confirmPaymentRequestDTO, token);

        return ResponseEntity.ok(confirmPaymentResponseDTO);
    }

    /**
     * Marks a specific payment confirmation as confirmed.
     *
     * @param paymentConfirmationId the ID of the payment confirmation to be marked as confirmed.
     * @return the updated payment confirmation response.
     */
    @PatchMapping("/confirm/{paymentConfirmationId}")
    public ResponseEntity<PaymentResponseDTO> markPaymentAsConfirmed(
            @PathVariable Long paymentConfirmationId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        PaymentResponseDTO confirmPaymentResponseDTO = paymentConfirmationService.markPaymentAsConfirmed(paymentConfirmationId, token);

        return ResponseEntity.ok(confirmPaymentResponseDTO);
    }

    /**
     * Deletes a specific payment confirmation.
     *
     * @param paymentConfirmationId the ID of the payment confirmation to be deleted.
     * @param deletePaymentConfirmationRequestDTO the DTO containing deletion request data.
     * @return a response message indicating the status of the deletion.
     */
    @DeleteMapping("/{paymentConfirmationId}")
    public ResponseEntity<?> deletePaymentConfirmation(@PathVariable Long paymentConfirmationId,
                                                       @Valid @ModelAttribute DeletePaymentConfirmationRequestDTO deletePaymentConfirmationRequestDTO) {

        // The paymentConfirmationId is manually added to the DTO for validation.
        deletePaymentConfirmationRequestDTO.setPaymentConfirmationId(paymentConfirmationId);

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        MessageResponseDTO messageResponseDTO = paymentConfirmationService.deletePaymentConfirmation(paymentConfirmationId, token);

        return ResponseEntity.ok(messageResponseDTO);
    }
}
