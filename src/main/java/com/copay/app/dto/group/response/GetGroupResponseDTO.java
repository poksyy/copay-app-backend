package com.copay.app.dto.group.response;

import java.util.List;

public class GetGroupResponseDTO {

	private List<GroupResponseDTO> groups;

	// Getters and Setters.
	public List<GroupResponseDTO> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupResponseDTO> groups) {
		this.groups = groups;
	}
}
