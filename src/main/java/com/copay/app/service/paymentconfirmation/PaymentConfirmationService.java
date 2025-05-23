package com.copay.app.service.paymentconfirmation;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.paymentconfirmation.request.ConfirmPaymentRequestDTO;
import com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO;
import com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO;

import java.util.List;

public interface PaymentConfirmationService {

    PaymentResponseDTO requestPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, String token);

    PaymentResponseDTO confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, String token);

    List<PaymentResponseDTO> getAllUserExpensesByGroupId(Long groupId, String token);

    List<PaymentResponseDTO> getUserExpenseIdsForUserInGroup(Long groupId, String token);

    List<ListUnconfirmedPaymentConfirmationResponseDTO> getAllUnconfirmedConfirmations(Long groupId);

    PaymentResponseDTO markPaymentAsConfirmed(Long confirmationId, String token);

    MessageResponseDTO deletePaymentConfirmation(Long confirmationId, String token);

}
