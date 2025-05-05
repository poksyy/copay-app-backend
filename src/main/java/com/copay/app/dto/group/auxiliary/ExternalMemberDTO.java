package com.copay.app.dto.group.auxiliary;

public class ExternalMemberDTO {

	private Long externalMembersId;
	private String externalMemberName;

	public ExternalMemberDTO() {}

	// Constructor used when mapping the DTO response.
	public ExternalMemberDTO(Long externalMembersId, String externalMemberName) {
		this.externalMembersId = externalMembersId;
		this.externalMemberName = externalMemberName;
	}

	// Getters and setters.
	public Long getExternalMembersId() {
		return externalMembersId;
	}

	public void setExternalMembersId(Long externalMembersId) {
		this.externalMembersId = externalMembersId;
	}

	public String getName() {
		return externalMemberName;
	}

	public void setName(String externalMemberName) {
		this.externalMemberName = externalMemberName;
	}
}
