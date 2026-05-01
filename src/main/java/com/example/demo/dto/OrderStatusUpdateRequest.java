package com.example.demo.dto;

import com.example.demo.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "OrderStatusUpdateRequest", description = "Payload for manually changing an order status.")
public class OrderStatusUpdateRequest {

    @Schema(description = "New status to apply to the order.", example = "SHIPPED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
