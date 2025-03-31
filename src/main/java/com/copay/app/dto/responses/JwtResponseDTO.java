package com.copay.app.dto.responses;

public class JwtResponseDTO {

	private String token;
	private String type = "Bearer";
	private long expiresIn;

	// Constructor.
	public JwtResponseDTO(String token, long expiresIn) {
		this.token = token;
		this.expiresIn = expiresIn;
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
}