package com.copay.app.repository.paymentconfirmation;

import com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO;
import com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.entity.relations.PaymentConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentConfirmationRepository extends JpaRepository<PaymentConfirmation, Long> {

    // Search by the UserExpense object's ID.
    Optional<PaymentConfirmation> findByUserExpense_UserExpenseId(Long userExpenseId);

    // Delete all PaymentConfirmations whose UserExpense has a specific Expense
    void deleteByUserExpense_ExpenseId(Expense expense);

    // If you want to get all instead of deleting directly.
    List<PaymentConfirmation> findByUserExpense_ExpenseId(Expense expense);

    // Get all user expenses by group id.
    @Query("""
    SELECT new com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO(
        p.paymentConfirmationId,
        ue.userExpenseId,
        p.confirmationAmount,
        p.confirmationDate,
        p.isConfirmed,
        p.confirmedAt
    )
    FROM PaymentConfirmation p
    JOIN p.userExpense ue
    WHERE ue.expenseId.groupId.groupId = :groupId
""")
    List<PaymentResponseDTO> findAllUserExpensesByGroupId(@Param("groupId") Long groupId);

    // Get debt_id by token user id and specific group id.
    // TODO: Implementation to business logic
    @Query("""
                SELECT new com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO(
                    p.paymentConfirmationId,
                    p.userExpense.userExpenseId,
                    p.confirmationAmount,
                    p.confirmationDate,
                    p.isConfirmed,
                    p.confirmedAt
                )
                FROM PaymentConfirmation p
                WHERE p.userExpense.expenseId.groupId.groupId = :groupId
                AND (p.userExpense.debtorUser.userId = :userId OR p.userExpense.creditorUser.userId = :userId)
            """)
    List<PaymentResponseDTO> findUserExpenseIdsByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // Get all unconfirmed payment confirmation requests.
    @Query("SELECT new com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO(p.userExpense.userExpenseId, p.confirmationAmount) " +
            "FROM PaymentConfirmation p " +
            "WHERE p.isConfirmed = false AND p.userExpense.expenseId.groupId.groupId = :groupId")
    List<ListUnconfirmedPaymentConfirmationResponseDTO> findUnconfirmedByGroupId(@Param("groupId") Long groupId);

}
