package com.copay.app.dto.group.auxiliary;

import com.fasterxml.jackson.annotation.JsonInclude;

public class InvitedRegisteredMemberDTO {

    private String phoneNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean payer;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPayer() {
        return payer;
    }

    public void setPayer(boolean payer) {
        this.payer = payer;
    }
}
