package com.copay.app.repository.paymentconfirmation;

import com.copay.app.entity.Expense;
import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentConfirmationRepository extends JpaRepository<GroupMember, GroupMemberId> {

//    void deleteByExpense(Expense expense);
}