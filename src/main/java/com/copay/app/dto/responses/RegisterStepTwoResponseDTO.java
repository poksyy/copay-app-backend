package com.copay.app.dto.responses;

public class RegisterStepTwoResponseDTO {

    private String token;
    private long expiresIn;
	private long userId;
    private String username;  
    private String phoneNumber;  
    private String email;

    // Constructor.
    public RegisterStepTwoResponseDTO(String token, long expiresIn, Long userId, String phoneNumber, String username, String email) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
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

    public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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
