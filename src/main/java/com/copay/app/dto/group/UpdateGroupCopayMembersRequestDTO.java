package com.copay.app.dto.group;

import java.util.List;
import com.copay.app.dto.group.auxiliary.CopayMemberDTO;
import jakarta.validation.constraints.NotNull;

public class UpdateGroupCopayMembersRequestDTO {

	@NotNull(message = "Copay members list must not be null.")
	private List<String> invitedCopayMembers;

	public List<String> getInvitedCopayMembers() {
		return invitedCopayMembers;
	}

	public void setInvitedCopayMembers(List<String> invitedCopayMembers) {
		this.invitedCopayMembers = invitedCopayMembers;
	}
}
