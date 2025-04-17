package com.copay.app.dto.group.response;

import java.util.List;

public class GetGroupResponseDTO {

	private List<CreateGroupResponseDTO> groups;

	// Getters and Setters.
	public List<CreateGroupResponseDTO> getGroups() {
		return groups;
	}

	public void setGroups(List<CreateGroupResponseDTO> groups) {
		this.groups = groups;
	}
}
