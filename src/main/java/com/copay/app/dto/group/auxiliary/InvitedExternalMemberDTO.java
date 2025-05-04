package com.copay.app.dto.group.auxiliary;

import com.fasterxml.jackson.annotation.JsonInclude;

public class InvitedExternalMemberDTO {

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean payer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPayer() {
        return payer;
    }

    public void setPayer(boolean payer) {
        this.payer = payer;
    }
}