package com.copay.app.dto.group.response;

import java.time.LocalDateTime;
import java.util.List;

import com.copay.app.dto.group.auxiliary.RegisteredMemberDTO;
import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.GroupOwnerDTO;

public class CreateGroupResponseDTO {

	private Long groupId;
	private String groupName;
	private String description;
	private Float estimatedPrice;
	private String currency;
	private LocalDateTime createdAt;
	private boolean userIsOwner;

	// Populated with GroupOwnerDTO data representing group groupOwner.
	private GroupOwnerDTO groupOwner;

	// Populated with CopayMemberDTO data representing the Copay members.
	private List<RegisteredMemberDTO> registeredMembers;
	// Populated with ExternalMemberDTO data representing the external members.
	private List<ExternalMemberDTO> externalMembers;

	// Getters and Setters
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getEstimatedPrice() {
		return estimatedPrice;
	}

	public void setEstimatedPrice(Float estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public boolean isUserIsOwner() {
	    return userIsOwner;
	}

	public void setUserIsOwner(boolean userIsOwner) {
	    this.userIsOwner = userIsOwner;
	}
	
	public GroupOwnerDTO getGroupOwner() {
		return groupOwner;
	}

	public void setGroupOwner(GroupOwnerDTO groupOwner) {
		this.groupOwner = groupOwner;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<RegisteredMemberDTO> getRegisteredMembers() {
		return registeredMembers;
	}

	public void setRegisteredMembers(List<RegisteredMemberDTO> registeredMembers) {
		this.registeredMembers = registeredMembers;
	}

	public List<ExternalMemberDTO> getExternalMembers() {
		return externalMembers;
	}

	public void setExternalMembers(List<ExternalMemberDTO> externalMembers) {
		this.externalMembers = externalMembers;
	}
}
