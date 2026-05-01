package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(name = "BatchRequest", description = "Payload for creating or updating a batch.")
public class BatchRequest {

    @ArraySchema(
            schema = @Schema(type = "string", example = "ORD-2026-0001"),
            arraySchema = @Schema(description = "Order numbers to include in the batch.", requiredMode = Schema.RequiredMode.REQUIRED)
    )
    @NotEmpty(message = "At least one order must be selected")
    private List<@NotBlank(message = "Order number is required") String> orderNumbers;

    public List<String> getOrderNumbers() {
        return orderNumbers;
    }

    public void setOrderNumbers(List<String> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }
}
