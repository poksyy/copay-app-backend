package com.copay.app.dto.group.auxiliary;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateGroupDTO {

	@NotBlank(message = "Group name is required")
	@Size(max = 25, message = "Group name must be no longer than 25 characters")
	private String groupName;

	@Size(max = 50, message = "Description must be no longer than 50 characters")
	private String description;

	@NotNull(message = "Estimated price is required")
	@DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
	@DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
	private Float estimatedPrice;

	@AssertTrue(message = "At least one list of members must be provided")
	public boolean isAtLeastOneMemberProvided() {

		return (externalMembers != null && !externalMembers.isEmpty())
				|| (registeredMembers != null && !registeredMembers.isEmpty());
	}

	private List<String> registeredMembers = new ArrayList<>();
	private List<String> externalMembers = new ArrayList<>();

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public List<String> getRegisteredMembers() {
		return registeredMembers;
	}

	public void setRegisteredMembers(List<String> registeredMembers) {
		this.registeredMembers = registeredMembers;
	}

	public List<String> getExternalMembers() {
		return externalMembers;
	}

	public void setExternalMembers(List<String> externalMembers) {
		this.externalMembers = externalMembers;
	}
}