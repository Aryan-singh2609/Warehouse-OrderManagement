package com.example.demo.dto;

import com.example.demo.entity.BatchOrderInfo;
import com.example.demo.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BatchOrderResponse", description = "Order summary embedded inside a batch response.")
public class BatchOrderResponse {

    @Schema(description = "Internal batch-order row id.", example = "501")
    private long id;
    @Schema(description = "Order number.", example = "ORD-2026-0001")
    private String orderNumber;
    @Schema(description = "Warehouse identifier.", example = "WH-01")
    private String warehouseId;
    @Schema(description = "Fulfillment center identifier.", example = "FC-BLR-01")
    private String fcId;
    @Schema(description = "Fulfillment center location name.", example = "Bengaluru South Hub")
    private String fcLocation;
    @Schema(description = "Client id.", example = "10")
    private long clientId;
    @Schema(description = "Client name.", example = "Northwind Retail")
    private String clientName;
    @Schema(description = "Billing address.", example = "221B Baker Street, London")
    private String billToAddress;
    @Schema(description = "Shipping address.", example = "7 Warehouse Avenue, Manchester")
    private String shipToAddress;
    @Schema(description = "Current order status.", example = "ALLOCATED")
    private OrderStatus orderStatus;
    @Schema(description = "Stock keeping unit.", example = "SKU-USB-SCAN-01")
    private String sku;
    @Schema(description = "Product identifier.", example = "PRD-1001")
    private String productId;
    @Schema(description = "Requested quantity.", example = "3")
    private int quantity;
    @Schema(description = "Fulfilled quantity.", example = "2")
    private int fulfilledQuantity;
    @Schema(description = "Back-ordered quantity.", example = "1")
    private int backOrderedQuantity;

    public BatchOrderResponse(
            long id,
            String orderNumber,
            String warehouseId,
            String fcId,
            String fcLocation,
            long clientId,
            String clientName,
            String billToAddress,
            String shipToAddress,
            OrderStatus orderStatus,
            String sku,
            String productId,
            int quantity,
            int fulfilledQuantity,
            int backOrderedQuantity
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.fcLocation = fcLocation;
        this.clientId = clientId;
        this.clientName = clientName;
        this.billToAddress = billToAddress;
        this.shipToAddress = shipToAddress;
        this.orderStatus = orderStatus;
        this.sku = sku;
        this.productId = productId;
        this.quantity = quantity;
        this.fulfilledQuantity = fulfilledQuantity;
        this.backOrderedQuantity = backOrderedQuantity;
    }

    public static BatchOrderResponse from(BatchOrderInfo batchOrderInfo) {
        return new BatchOrderResponse(
                batchOrderInfo.getId(),
                batchOrderInfo.getOrderNumber(),
                batchOrderInfo.getWarehouseId(),
                batchOrderInfo.getFcId(),
                batchOrderInfo.getFcLocation(),
                batchOrderInfo.getClientId(),
                batchOrderInfo.getClientName(),
                batchOrderInfo.getBillToAddress(),
                batchOrderInfo.getShipToAddress(),
                batchOrderInfo.getOrderStatus(),
                batchOrderInfo.getSku(),
                batchOrderInfo.getProductId(),
                batchOrderInfo.getQuantity(),
                batchOrderInfo.getFulfilledQuantity(),
                batchOrderInfo.getBackOrderedQuantity()
        );
    }

    public long getId() {
        return id;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
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
}
