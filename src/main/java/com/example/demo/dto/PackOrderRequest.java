package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "PackOrderRequest", description = "Payload used to pack an order and allocate a box.")
public class PackOrderRequest {

    @Schema(description = "Measured order weight in pounds.", example = "18.75", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
