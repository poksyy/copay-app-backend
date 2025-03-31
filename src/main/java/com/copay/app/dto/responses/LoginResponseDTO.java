package com.copay.app.dto.responses;

public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private long expiresIn;
    private String username;  
    private String phoneNumber;  
    private String email;
	private String isLogin;

    // Constructor.
    public LoginResponseDTO(String token, long expiresIn, String phoneNumber, String username, String email, String isLogin) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isLogin = isLogin;
    }

    // Getters and Setters.
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    
    public String getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(String isLogin) {
		this.isLogin = isLogin;
	}
}
