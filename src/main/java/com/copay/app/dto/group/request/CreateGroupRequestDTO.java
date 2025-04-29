package com.copay.app.dto.group.request;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequestDTO {

	@NotNull(message = "Creator user id is required")
	private Long createdBy;

	@NotBlank(message = "Group name is required")
	@Size(max = 25, message = "Group name must be no longer than 25 characters")
	private String name;

	@Size(max = 50, message = "Description must be no longer than 50 characters")
	private String description;

	@NotNull(message = "Estimated price is required")
	@DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
	@DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
	private Float estimatedPrice;

	@NotBlank(message = "Currency is required")
	@Size(max = 10, message = "Currency must be no longer than 10 characters")
	private String currency;

	@Size(max = 255, message = "Image URL must be no longer than 255 characters")
	private String imageUrl;

	@Size(max = 50, message = "Image provider must be no longer than 50 characters")
	private String imageProvider;

	// Initialize the ArrayLists null by default.
	private List<String> invitedRegisteredMembers = new ArrayList<>();
	private List<String> invitedExternalMembers = new ArrayList<>();

	private Long paidByRegisteredMemberId;
	private Long paidByExternalMemberId;

	@AssertTrue(message = "At least one list of members must be provided")
	public boolean isAtLeastOneMemberProvided() {
		// Validates that at least 1 of both variables are not null or empty
		return !invitedExternalMembers.isEmpty() || !invitedRegisteredMembers.isEmpty();
	}

	// Validation to ensure that only one payer is selected
	@AssertTrue(message = "Only one payer must be selected (either from registered or external members, not both)")
	public boolean isOnlyOnePayer() {
		// Ensure that only one of the following fields is not null
		return (paidByRegisteredMemberId != null && paidByExternalMemberId == null) ||
				(paidByRegisteredMemberId == null && paidByExternalMemberId != null);
	}

	// Getters and Setters.
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageProvider() {
		return imageProvider;
	}

	public void setImageProvider(String imageProvider) {
		this.imageProvider = imageProvider;
	}

	public List<String> getInvitedExternalMembers() {
		return invitedExternalMembers;
	}

	public void setInvitedExternalMembers(List<String> invitedExternalMembers) {
		this.invitedExternalMembers = invitedExternalMembers;
	}

	public List<String> getInvitedRegisteredMembers() {
		return invitedRegisteredMembers;
	}

	public void setInvitedRegisteredMembers(List<String> invitedRegisteredMembers) {
		this.invitedRegisteredMembers = invitedRegisteredMembers;
	}

	public Long getPaidByRegisteredMemberId() {
		return paidByRegisteredMemberId;
	}

	public void setPaidByRegisteredMemberId(Long paidByRegisteredMemberId) {
		this.paidByRegisteredMemberId = paidByRegisteredMemberId;
	}

	public Long getPaidByExternalMemberId() {
		return paidByExternalMemberId;
	}

	public void setPaidByExternalMemberId(Long paidByExternalMemberId) {
		this.paidByExternalMemberId = paidByExternalMemberId;
	}

	// toString method with debug purposes.
    @Override
    public String toString() {
        return "CreateGroupRequestDTO{" +
               "groupName='" + name + '\'' +
               ", description='" + description + '\'' +
               ", currency='" + currency + '\'' +
               ", estimatedPrice=" + estimatedPrice +
               ", invitedRegisteredMemebers=" + invitedRegisteredMembers +
               ", invitedExternalMembers=" + invitedExternalMembers +
               ", createdBy=" + createdBy +
               '}';
    }
}
