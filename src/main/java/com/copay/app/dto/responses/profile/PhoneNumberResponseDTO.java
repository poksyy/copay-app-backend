package com.copay.app.dto.responses.profile;

import com.copay.app.entity.User;

public class PhoneNumberResponseDTO {
    private Long userId;
    private String phoneNumber;

    public PhoneNumberResponseDTO(User user) {
        this.userId = user.getUserId();
        this.phoneNumber = user.getPhoneNumber();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}