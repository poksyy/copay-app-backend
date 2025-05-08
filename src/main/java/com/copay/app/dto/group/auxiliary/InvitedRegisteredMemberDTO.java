package com.copay.app.dto.group.auxiliary;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InvitedRegisteredMemberDTO {

    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 digits")
    private String phoneNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean creditor;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isCreditor() {
        return creditor;
    }

    public void setIsCreditor(boolean creditor) {
        this.creditor = creditor;
    }
}
