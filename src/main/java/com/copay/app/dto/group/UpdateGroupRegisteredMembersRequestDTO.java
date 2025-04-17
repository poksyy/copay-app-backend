package com.copay.app.dto.group;

import java.util.List;
import com.copay.app.dto.group.auxiliary.RegisteredMemberDTO;
import jakarta.validation.constraints.NotNull;

public class UpdateGroupRegisteredMembersRequestDTO {

	@NotNull(message = "Copay members list must not be null.")
	private List<String> invitedRegisteredMembers;

	public List<String> getInvitedRegisteredMembers() {
		return invitedRegisteredMembers;
	}

	public void setInvitedRegisteredMembers(List<String> invitedRegisteredMembers) {
		this.invitedRegisteredMembers = invitedRegisteredMembers;
	}
}
