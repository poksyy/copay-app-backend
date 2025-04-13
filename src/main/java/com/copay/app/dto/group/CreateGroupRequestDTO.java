package com.copay.app.dto.group;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequestDTO {

	@NotNull(message = "Creator user id is required")
	private Long createdBy;

	@NotBlank(message = "Group name is required")
	@Size(max = 25, message = "Group name must be no longer than 25 characters")
	private String groupName;

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
	private List<String> invitedExternalMembers = new ArrayList<>();
	private List<String> invitedCopayMembers = new ArrayList<>();

	@AssertTrue(message = "At least one list of members must be provided")
	public boolean isAtLeastOneMemberProvided() {
		// Validates that at least 1 of both variables are not null or empty
		return !invitedExternalMembers.isEmpty() || !invitedCopayMembers.isEmpty();
	}

	// Getters and Setters.
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public List<String> getInvitedCopayMembers() {
		return invitedCopayMembers;
	}

	public void setInvitedCopayMembers(List<String> invitedCopayMembers) {
		this.invitedCopayMembers = invitedCopayMembers;
	}

	// toString method with debug purposes.
    @Override
    public String toString() {
        return "CreateGroupRequestDTO{" +
               "groupName='" + groupName + '\'' +
               ", description='" + description + '\'' +
               ", currency='" + currency + '\'' +
               ", estimatedPrice=" + estimatedPrice +
               ", invitedCopayMembers=" + invitedCopayMembers +
               ", invitedExternalMembers=" + invitedExternalMembers +
               ", createdBy=" + createdBy +
               '}';
    }
}
