package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "batch_order_info")
public class BatchOrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_info_id", nullable = false)
    private BatchInfo batchInfo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_info_id", nullable = false)
    private OrderInfo orderInfo;

    @Column(nullable = false)
    private long orderItemInfoId;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String warehouseId;

    @Column(nullable = false)
    private String fcId;

    @Column(nullable = false)
    private String fcLocation;

    @Column(nullable = false)
    private long clientId;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false, length = 1024)
    private String billToAddress;

    @Column(nullable = false, length = 1024)
    private String shipToAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column
    private Long pickerId;

    @Column
    private String pickerName;

    @Column
    private String pickerEmail;

    @Column
    private String pickerEmployeeId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int fulfilledQuantity;

    @Column(nullable = false)
    private int backOrderedQuantity;

    protected BatchOrderInfo() {
    }

    public BatchOrderInfo(OrderInfo orderInfo, OrderItemInfo item) {
        this.orderInfo = orderInfo;
        syncFrom(orderInfo, item);
    }

    public long getId() {
        return id;
    }

    public BatchInfo getBatchInfo() {
        return batchInfo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public long getOrderItemInfoId() {
        return orderItemInfoId;
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

    public Long getPickerId() {
        return pickerId;
    }

    public String getPickerName() {
        return pickerName;
    }

    public String getPickerEmail() {
        return pickerEmail;
    }

    public String getPickerEmployeeId() {
        return pickerEmployeeId;
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

    public void attachTo(BatchInfo batchInfo) {
        this.batchInfo = batchInfo;
    }

    public void syncFrom(OrderInfo orderInfo, OrderItemInfo item) {
        this.orderInfo = orderInfo;
        this.orderItemInfoId = item.getId();
        this.orderNumber = orderInfo.getOrderNumber();
        this.warehouseId = orderInfo.getWarehouseId();
        this.fcId = orderInfo.getFcId();
        this.fcLocation = orderInfo.getFcLocation();
        this.clientId = orderInfo.getClient().getId();
        this.clientName = orderInfo.getClient().getName();
        this.billToAddress = orderInfo.getBillToAddress();
        this.shipToAddress = orderInfo.getShipToAddress();
        this.orderStatus = orderInfo.getStatus();
        this.pickerId = orderInfo.getPicker() == null ? null : orderInfo.getPicker().getId();
        this.pickerName = orderInfo.getPicker() == null ? null : orderInfo.getPicker().getName();
        this.pickerEmail = orderInfo.getPicker() == null ? null : orderInfo.getPicker().getEmail();
        this.pickerEmployeeId = orderInfo.getPicker() == null ? null : orderInfo.getPicker().getEmployeeId();
        this.sku = item.getSku();
        this.productId = item.getProductId();
        this.quantity = item.getQuantity();
        this.fulfilledQuantity = item.getFulfilledQuantity();
        this.backOrderedQuantity = item.getQuantity() - item.getFulfilledQuantity();
    }
}
