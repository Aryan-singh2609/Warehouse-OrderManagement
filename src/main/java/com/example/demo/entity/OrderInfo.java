package com.example.demo.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_info")
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String warehouseId;

    @Column(nullable = false)
    private String fcId;

    @Column(nullable = false)
    private String fcLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, length = 1024)
    private String billToAddress;

    @Column(nullable = false, length = 1024)
    private String shipToAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picker_id")
    private Picker picker;

    @Column
    private LocalDateTime pickedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private BoxCategory boxCategory;

    @Column(unique = true)
    private String boxId;

    @Column(precision = 10, scale = 2)
    private BigDecimal packedWeight;

    @Column
    private LocalDateTime packedAt;

    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemInfo> items = new ArrayList<>();

    protected OrderInfo() {
    }

    public OrderInfo(
            String orderNumber,
            String warehouseId,
            String fcId,
            String fcLocation,
            Client client,
            String billToAddress,
            String shipToAddress,
            OrderStatus status
    ) {
        this.orderNumber = orderNumber;
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.fcLocation = fcLocation;
        this.client = client;
        this.billToAddress = billToAddress;
        this.shipToAddress = shipToAddress;
        this.status = status;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
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

    public Client getClient() {
        return client;
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

    public Picker getPicker() {
        return picker;
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

    public List<OrderItemInfo> getItems() {
        return items;
    }

    public void addItem(OrderItemInfo item) {
        items.add(item);
        item.attachTo(this);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void assignPicker(Picker picker) {
        this.picker = picker;
        this.status = OrderStatus.ASSIGNED_FOR_PICKING;
    }

    public void clearPickerAssignment() {
        this.picker = null;
        this.status = OrderStatus.CREATED;
        this.pickedAt = null;
        this.boxCategory = null;
        this.boxId = null;
        this.packedWeight = null;
        this.packedAt = null;
    }

    public void markPicked() {
        this.status = OrderStatus.PICKED;
        this.pickedAt = LocalDateTime.now();
        this.boxCategory = null;
        this.boxId = null;
        this.packedWeight = null;
        this.packedAt = null;
    }

    public void markPacked(BoxCategory boxCategory, String boxId, BigDecimal packedWeight) {
        this.status = OrderStatus.PACKED;
        this.boxCategory = boxCategory;
        this.boxId = boxId;
        this.packedWeight = packedWeight;
        this.packedAt = LocalDateTime.now();
    }
}
