package com.copay.app.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

public class GroupRequestDTO {

    @NotBlank(message = "Group name is required")
    @Size(max = 25, message = "Group name must be no longer than 25 characters")
    private String groupName;

    @NotNull(message = "Creator user id is required")
    private Long createdBy;

    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must be no longer than 10 characters")
    private String currency;

    @Size(max = 50, message = "Description must be no longer than 50 characters")
    private String description;

    @NotNull(message = "Estimated price is required")
    @DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
    @DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
    private Float estimatedPrice;

    @Size(max = 255, message = "Image URL must be no longer than 255 characters")
    private String imageUrl;

    @Size(max = 50, message = "Image provider must be no longer than 50 characters")
    private String imageProvider;

    // Include the phone numbers of the invited users.
    private List<String> invitedMembers; 

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

    public List<String> getInvitedMembers() {
        return invitedMembers;
    }

    public void setInvitedMembers(List<String> invitedMembers) {
        this.invitedMembers = invitedMembers;
    }
}
