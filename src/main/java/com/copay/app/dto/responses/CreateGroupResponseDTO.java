package com.copay.app.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import com.copay.app.dto.group.auxiliary.CopayMemberDTO;
import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.GroupOwnerDTO;

public class CreateGroupResponseDTO {

	private Long groupId;
	private String groupName;
	private String description;
	private Float estimatedPrice;
	private String currency;
	private LocalDateTime createdAt;

	// Populated with GroupOwnerDTO data representing group groupOwner.
	private GroupOwnerDTO groupOwner;

	// Populated with CopayMemberDTO data representing the Copay members.
	private List<CopayMemberDTO> copayMembers;
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

	public GroupOwnerDTO getGroupOwner() {
		return groupOwner;
	}

	public void setGroupOwner(GroupOwnerDTO groupOwner) {
		this.groupOwner = groupOwner;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<CopayMemberDTO> getCopayMembers() {
		return copayMembers;
	}

	public void setCopayMembers(List<CopayMemberDTO> copayMembers) {
		this.copayMembers = copayMembers;
	}

	public List<ExternalMemberDTO> getExternalMembers() {
		return externalMembers;
	}

	public void setExternalMembers(List<ExternalMemberDTO> externalMembers) {
		this.externalMembers = externalMembers;
	}
}
