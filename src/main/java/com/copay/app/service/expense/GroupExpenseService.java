package com.copay.app.service.expense;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;

public interface GroupExpenseService {

    void initializeExpenseFromGroup(Group group, Float estimatedPrice, User userCreditor, ExternalMember externalCreditor);

    void updateExpenseTotalAmount(Group group, Float newPrice);

    void updateExpenseGroupMembers(Group group, User userCreditor, ExternalMember externalCreditor);
}
