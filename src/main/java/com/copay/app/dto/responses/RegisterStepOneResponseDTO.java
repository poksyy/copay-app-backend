package com.copay.app.dto.responses;

public class RegisterStepOneResponseDTO {

    private String token;
    private long expiresIn;
    private String username;  
    private String email;


    // Constructor.
    public RegisterStepOneResponseDTO(String token, long expiresIn, String username, String email) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
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
}
