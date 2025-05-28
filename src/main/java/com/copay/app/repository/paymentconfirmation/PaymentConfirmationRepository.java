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

    // === Standard Spring Data methods ===

    /**
     * Finds all PaymentConfirmations by UserExpenseId and isConfirmed status.
     *
     * @param userExpenseId the ID of the UserExpense.
     * @param isConfirmed whether the PaymentConfirmation is confirmed.
     * @return a list of matching PaymentConfirmations.
     */
    List<PaymentConfirmation> findAllByUserExpense_UserExpenseIdAndIsConfirmed(Long userExpenseId, boolean isConfirmed);

    // Search the payment confirmations that are not confirmed.
    Optional<PaymentConfirmation> findFirstByUserExpense_UserExpenseIdAndIsConfirmedFalse(Long userExpenseId);

    // Delete all PaymentConfirmations, whose UserExpense has a specific Expense
    void deleteByUserExpense_ExpenseId(Expense expense);

    // If you want to get all instead of deleting directly.
    List<PaymentConfirmation> findByUserExpense_ExpenseId(Expense expense);


    // === Custom queries with @Query ===

// Get all user expenses by group id.

    /**
     * NOTE: LEFT JOINs are used intentionally in this query to avoid excluding records
     * where either debtorUser or creditorUser (or their external counterparts) are null.
     * Without LEFT JOIN, JPQL performs implicit INNER JOINs which filter out rows
     * with null relationships, resulting in missing data even when some values are present.
     */
    @Query("""
            SELECT new com.copay.app.dto.paymentconfirmation.response.PaymentResponseDTO(
                p.paymentConfirmationId,
                ue.userExpenseId,
                p.confirmationAmount,
                p.confirmationDate,
                p.isConfirmed,
                p.confirmedAt,
                CASE 
                    WHEN ue.debtorUser IS NOT NULL THEN ue.debtorUser.username 
                    WHEN ue.debtorExternalMember IS NOT NULL THEN ue.debtorExternalMember.name 
                    ELSE 'Unknown' 
                END,
                CASE 
                    WHEN ue.creditorUser IS NOT NULL THEN ue.creditorUser.username 
                    WHEN ue.creditorExternalMember IS NOT NULL THEN ue.creditorExternalMember.name 
                    ELSE 'Unknown' 
                END
            )
            FROM PaymentConfirmation p
            JOIN p.userExpense ue
            LEFT JOIN ue.debtorUser du
            LEFT JOIN ue.creditorUser cu
            LEFT JOIN ue.debtorExternalMember dem
            LEFT JOIN ue.creditorExternalMember cem
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
            p.confirmedAt,
            p.userExpense.debtorUser.username,
            p.userExpense.creditorUser.username
        )
        FROM PaymentConfirmation p
        WHERE p.userExpense.expenseId.groupId.groupId = :groupId
        AND (p.userExpense.debtorUser.userId = :userId OR p.userExpense.creditorUser.userId = :userId)
    """)
    List<PaymentResponseDTO> findUserExpenseIdsByGroupIdAndUserId(
            @Param("groupId") Long groupId,
            @Param("userId") Long userId
    );


    // Get all unconfirmed payment confirmation requests.
    @Query("""
        SELECT new com.copay.app.dto.paymentconfirmation.response.ListUnconfirmedPaymentConfirmationResponseDTO(
            p.paymentConfirmationId,
            p.userExpense.userExpenseId,
            p.confirmationAmount,
            p.userExpense.debtorUser.username
        )
        FROM PaymentConfirmation p
        WHERE p.isConfirmed = false AND p.userExpense.expenseId.groupId.groupId = :groupId
    """)
    List<ListUnconfirmedPaymentConfirmationResponseDTO> findUnconfirmedByGroupId(@Param("groupId") Long groupId);

    @Query("""
        SELECT SUM(p.confirmationAmount)
        FROM PaymentConfirmation p
        WHERE p.isConfirmed = true 
        AND p.userExpense.debtorUser.userId = :userId
    """)
    Float getTotalSpentByUserId(@Param("userId") Long userId);
}
