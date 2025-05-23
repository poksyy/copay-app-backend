package com.copay.app.dto.group.request;

import java.util.List;

import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import jakarta.validation.constraints.NotNull;

public class UpdateGroupExternalMembersRequestDTO {

	@NotNull(message = "External members list must not be null.")
	private List<ExternalMemberDTO> invitedExternalMembers;

	public List<ExternalMemberDTO> getInvitedExternalMembers() {
		return invitedExternalMembers;
	}

	public void setInvitedExternalMembers(List<ExternalMemberDTO> invitedExternalMembers) {
		this.invitedExternalMembers = invitedExternalMembers;
	}
}
