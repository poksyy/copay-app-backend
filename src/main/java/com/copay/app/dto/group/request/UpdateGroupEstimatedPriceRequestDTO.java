package com.copay.app.dto.group.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdateGroupEstimatedPriceRequestDTO {

    @NotNull(message = "Estimated price is required")
    @DecimalMin(value = "0", message = "Estimated price must be greater than or equal to 0")
    @DecimalMax(value = "10000000", message = "Estimated price must be smaller than or equal to 10000000")
    private Float estimatedPrice;

    public Float getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Float estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
