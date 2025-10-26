package com.copay.app.repository.expense;

import com.copay.app.dto.expense.response.UserExpenseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.UserExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserExpenseRepository extends JpaRepository<UserExpense, Long> {

    void deleteByExpenseId(Expense expenseId);

    @Query("""
    SELECT new com.copay.app.dto.expense.response.UserExpenseDTO(
        ue.userExpenseId,
        ue.debtorUser.userId,
        ue.debtorExternalMember.externalMembersId,
        ue.amount)
    FROM UserExpense ue
    WHERE ue.expenseId.groupId.groupId = :groupId
""")
    List<UserExpenseDTO> findAllByGroupId(@Param("groupId") Long groupId);


    Optional<UserExpense> findByExpenseIdAndDebtorUser(Expense expenseId, User user);

    Optional<UserExpense> findByExpenseIdAndDebtorExternalMember(Expense expenseId, ExternalMember externalMember);

    List<UserExpense> findByExpenseId(Expense expenseId);

    @Query("""
    SELECT (ue.amount)
    FROM UserExpense ue
    WHERE ue.debtorUser.userId = :userId
    """)
    Float getTotalDebtAsDebtor(@Param("userId") Long userId);

    @Query("""
    SELECT COALESCE(SUM(ue.amount), 0)
    FROM UserExpense ue
    WHERE ue.creditorUser.userId = :userId
      AND (ue.debtorUser.userId IS NULL OR ue.debtorUser.userId <> :userId)
""")
    Float getTotalOwedToUser(@Param("userId") Long userId);
}
