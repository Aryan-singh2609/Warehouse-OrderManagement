package com.example.demo.dto;

import com.example.demo.entity.OrderItemInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OrderItemResponse", description = "Order line item returned by the API.")
public class OrderItemResponse {

    @Schema(description = "Order number that owns the line item.", example = "ORD-2026-0001")
    private String orderNumber;
    @Schema(description = "Stock keeping unit.", example = "SKU-USB-SCAN-01")
    private String sku;
    @Schema(description = "Product identifier.", example = "PRD-1001")
    private String productId;
    @Schema(description = "Requested quantity.", example = "3")
    private int quantity;
    @Schema(description = "Quantity fulfilled from stock.", example = "2")
    private int fulfilledQuantity;
    @Schema(description = "Quantity remaining on back order.", example = "1")
    private int backOrderedQuantity;
    @Schema(description = "Client id associated with the line item.", example = "10")
    private long clientId;

    public OrderItemResponse(
            String orderNumber,
            String sku,
            String productId,
            int quantity,
            int fulfilledQuantity,
            int backOrderedQuantity,
            long clientId
    ) {
        this.orderNumber = orderNumber;
        this.sku = sku;
        this.productId = productId;
        this.quantity = quantity;
        this.fulfilledQuantity = fulfilledQuantity;
        this.backOrderedQuantity = backOrderedQuantity;
        this.clientId = clientId;
    }

    public static OrderItemResponse from(OrderItemInfo item) {
        return new OrderItemResponse(
                item.getOrderNumber(),
                item.getSku(),
                item.getProductId(),
                item.getQuantity(),
                item.getFulfilledQuantity(),
                item.getQuantity() - item.getFulfilledQuantity(),
                item.getClientId()
        );
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSku() {
        return sku;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFulfilledQuantity() {
        return fulfilledQuantity;
    }

    public int getBackOrderedQuantity() {
        return backOrderedQuantity;
    }

    public long getClientId() {
        return clientId;
    }
}
