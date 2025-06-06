package com.copay.app.dto.group.request;

import com.copay.app.dto.group.auxiliary.InvitedExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.InvitedRegisteredMemberDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class CreateGroupRequestDTO {

	@NotNull(message = "Creator user must not be null")
	private Long createdBy;

	@NotBlank(message = "Group name must not be null")
	@Size(min = 3, max = 25, message = "Group name must be between 3 and 25 digits")
	private String name;

	@Size(max = 150, message = "Description must be no longer than 150 characters")
	private String description;

	@NotNull(message = "Estimated price must not be null")
	@DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
	@DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
	private Float estimatedPrice;

	@NotBlank(message = "Currency must not be null")
	@Size(max = 10, message = "Currency must be no longer than 10 characters")
	private String currency;

	@Size(max = 255, message = "Image URL must be no longer than 255 characters")
	private String imageUrl;

	@Size(max = 50, message = "Image provider must be no longer than 50 characters")
	private String imageProvider;

	// Initialize the ArrayLists null by default and ensure they are valid.
	@Valid
	private List<InvitedRegisteredMemberDTO> invitedRegisteredMembers;
	@Valid
	private List<InvitedExternalMemberDTO> invitedExternalMembers;

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

	public List<InvitedExternalMemberDTO> getInvitedExternalMembers() {
		return invitedExternalMembers;
	}

	public void setInvitedExternalMembers(List<InvitedExternalMemberDTO> invitedExternalMembers) {
		this.invitedExternalMembers = invitedExternalMembers;
	}

	public List<InvitedRegisteredMemberDTO> getInvitedRegisteredMembers() {
		return invitedRegisteredMembers;
	}

	public void setInvitedRegisteredMembers(List<InvitedRegisteredMemberDTO> invitedRegisteredMembers) {
		this.invitedRegisteredMembers = invitedRegisteredMembers;
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
