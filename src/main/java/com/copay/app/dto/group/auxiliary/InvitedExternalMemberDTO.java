package com.copay.app.dto.group.auxiliary;

import com.fasterxml.jackson.annotation.JsonInclude;

public class InvitedExternalMemberDTO {

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean creditor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCreditor() {
        return creditor;
    }

    public void setCreditor(boolean creditor) {
        this.creditor = creditor;
    }
}