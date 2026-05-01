package com.example.demo.dto;

import com.example.demo.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(name = "OrderRequest", description = "Payload for creating an order.")
public class OrderRequest {

    @Schema(description = "Fulfillment center identifier that will process the order.", example = "FC-BLR-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "FC ID is required")
    private String fcId;

    @Schema(description = "Client id placing the order.", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @Schema(description = "Billing address for the order.", example = "221B Baker Street, London", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Bill to address is required")
    private String billToAddress;

    @Schema(description = "Shipping address for the order.", example = "7 Warehouse Avenue, Manchester", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Ship to address is required")
    private String shipToAddress;

    @Schema(description = "Initial order status. If omitted, the service will apply its default workflow status.", example = "PENDING", nullable = true)
    private OrderStatus status;

    @ArraySchema(schema = @Schema(implementation = OrderItemRequest.class), arraySchema = @Schema(description = "Order line items.", requiredMode = Schema.RequiredMode.REQUIRED))
    @Valid
    @NotEmpty(message = "At least one order item is required")
    private List<OrderItemRequest> items;

    public String getFcId() {
        return fcId;
    }

    public void setFcId(String fcId) {
        this.fcId = fcId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getBillToAddress() {
        return billToAddress;
    }

    public void setBillToAddress(String billToAddress) {
        this.billToAddress = billToAddress;
    }

    public String getShipToAddress() {
        return shipToAddress;
    }

    public void setShipToAddress(String shipToAddress) {
        this.shipToAddress = shipToAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
