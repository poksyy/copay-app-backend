package com.copay.app.dto.group.auxiliary;

public class RegisteredMemberDTO {

	private Long id;
	private String phoneNumber;

	// Constructor used when mapping the DTO response.
	public RegisteredMemberDTO(Long id, String phoneNumber) {
		this.id = id;
		this.phoneNumber = phoneNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}