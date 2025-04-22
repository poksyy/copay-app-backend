package com.copay.app.dto.group.auxiliary;

public class RegisteredMemberDTO {

	private Long registeredMemberId;
	private String username;
	private String phoneNumber;

	// Constructor used when mapping the DTO response.
	public RegisteredMemberDTO(Long registeredMemberId, String username, String phoneNumber) {
		this.registeredMemberId = registeredMemberId;
		this.username = username;
		this.phoneNumber = phoneNumber;
	}

	public Long getRegisteredMemberId() {
		return registeredMemberId;
	}

	public void setRegisteredMemberId(Long registeredMemberId) {
		this.registeredMemberId = registeredMemberId;
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
}