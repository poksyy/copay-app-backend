package com.copay.app.dto.responses;

public class RegisterStepTwoResponseDTO {

    private String token;
    private long expiresIn;
    private String username;  
    private String phoneNumber;  
    private String email;

    // Constructor.
    public RegisterStepTwoResponseDTO(String token, long expiresIn, String phoneNumber, String username, String email) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters and Setters.
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
