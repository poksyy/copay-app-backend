package com.copay.app.dto.group.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class UpdateGroupRegisteredMembersRequestDTO {

	@NotNull(message = "Registered members list must not be null.")
	private List<String> invitedRegisteredMembers;

	public List<String> getInvitedRegisteredMembers() {
		return invitedRegisteredMembers;
	}

	public void setInvitedRegisteredMembers(List<String> invitedRegisteredMembers) {
		this.invitedRegisteredMembers = invitedRegisteredMembers;
	}
}
