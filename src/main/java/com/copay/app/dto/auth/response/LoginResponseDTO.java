package com.copay.app.dto.auth.response;

public class LoginResponseDTO {

    private String token;
    private long expiresIn;
    private Long userId;
    private String username;  
    private String phoneNumber;  
    private String email;
	private String isLogin;

    // Constructor.
    public LoginResponseDTO(String token, long expiresIn, Long userId, String phoneNumber, String username, String email, String isLogin) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.username = username;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
