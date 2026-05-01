package com.example.demo.dto;

import com.example.demo.entity.BoxCategory;
import com.example.demo.entity.OrderInfo;
import com.example.demo.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "OrderResponse", description = "Order returned by the API.")
public class OrderResponse {

    @Schema(description = "Unique order number.", example = "ORD-2026-0001")
    private String orderNumber;
    @Schema(description = "Warehouse identifier.", example = "WH-01")
    private String warehouseId;
    @Schema(description = "Fulfillment center identifier.", example = "FC-BLR-01")
    private String fcId;
    @Schema(description = "Fulfillment center location name.", example = "Bengaluru South Hub")
    private String fcLocation;
    @Schema(description = "Client id that placed the order.", example = "10")
    private long clientId;
    @Schema(description = "Client name.", example = "Northwind Retail")
    private String clientName;
    @Schema(description = "Billing address.", example = "221B Baker Street, London")
    private String billToAddress;
    @Schema(description = "Shipping address.", example = "7 Warehouse Avenue, Manchester")
    private String shipToAddress;
    @Schema(description = "Current order status.", example = "PENDING")
    private OrderStatus status;
    @Schema(description = "Timestamp when the order was created.", example = "2026-04-29T10:15:30")
    private LocalDateTime createdAt;
    @Schema(description = "Assigned picker id, if assigned.", example = "22", nullable = true)
    private Long pickerId;
    @Schema(description = "Assigned picker name, if assigned.", example = "Riya Sharma", nullable = true)
    private String pickerName;
    @Schema(description = "Timestamp when the order was picked, if available.", example = "2026-04-29T12:30:00", nullable = true)
    private LocalDateTime pickedAt;
    @Schema(description = "Allocated box category, if packed.", example = "SMALL_BOX", nullable = true)
    private BoxCategory boxCategory;
    @Schema(description = "Allocated unique box id, if packed.", example = "BOX-SMALL-7F3A91C2", nullable = true)
    private String boxId;
    @Schema(description = "Measured packed order weight in pounds, if packed.", example = "18.75", nullable = true)
    private BigDecimal packedWeight;
    @Schema(description = "Timestamp when the order was packed, if available.", example = "2026-04-29T13:05:00", nullable = true)
    private LocalDateTime packedAt;
    @ArraySchema(schema = @Schema(implementation = OrderItemResponse.class), arraySchema = @Schema(description = "Order line items."))
    private List<OrderItemResponse> items;

    public OrderResponse(
            String orderNumber,
            String warehouseId,
            String fcId,
            String fcLocation,
            long clientId,
            String clientName,
            String billToAddress,
            String shipToAddress,
            OrderStatus status,
            LocalDateTime createdAt,
            Long pickerId,
            String pickerName,
            LocalDateTime pickedAt,
            BoxCategory boxCategory,
            String boxId,
            BigDecimal packedWeight,
            LocalDateTime packedAt,
            List<OrderItemResponse> items
    ) {
        this.orderNumber = orderNumber;
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.fcLocation = fcLocation;
        this.clientId = clientId;
        this.clientName = clientName;
        this.billToAddress = billToAddress;
        this.shipToAddress = shipToAddress;
        this.status = status;
        this.createdAt = createdAt;
        this.pickerId = pickerId;
        this.pickerName = pickerName;
        this.pickedAt = pickedAt;
        this.boxCategory = boxCategory;
        this.boxId = boxId;
        this.packedWeight = packedWeight;
        this.packedAt = packedAt;
        this.items = items;
    }

    public static OrderResponse from(OrderInfo orderInfo) {
        return new OrderResponse(
                orderInfo.getOrderNumber(),
                orderInfo.getWarehouseId(),
                orderInfo.getFcId(),
                orderInfo.getFcLocation(),
                orderInfo.getClient().getId(),
                orderInfo.getClient().getName(),
                orderInfo.getBillToAddress(),
                orderInfo.getShipToAddress(),
                orderInfo.getStatus(),
                orderInfo.getCreatedAt(),
                orderInfo.getPicker() == null ? null : orderInfo.getPicker().getId(),
                orderInfo.getPicker() == null ? null : orderInfo.getPicker().getName(),
                orderInfo.getPickedAt(),
                orderInfo.getBoxCategory(),
                orderInfo.getBoxId(),
                orderInfo.getPackedWeight(),
                orderInfo.getPackedAt(),
                orderInfo.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getFcId() {
        return fcId;
    }

    public String getFcLocation() {
        return fcLocation;
    }

    public long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getBillToAddress() {
        return billToAddress;
    }

    public String getShipToAddress() {
        return shipToAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getPickerId() {
        return pickerId;
    }

    public String getPickerName() {
        return pickerName;
    }

    public LocalDateTime getPickedAt() {
        return pickedAt;
    }

    public BoxCategory getBoxCategory() {
        return boxCategory;
    }

    public String getBoxId() {
        return boxId;
    }

    public BigDecimal getPackedWeight() {
        return packedWeight;
    }

    public LocalDateTime getPackedAt() {
        return packedAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
