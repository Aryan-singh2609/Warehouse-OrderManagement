package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_item_info")
public class OrderItemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_info_id", nullable = false)
    private OrderInfo orderInfo;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int fulfilledQuantity;

    @Column(nullable = false)
    private long clientId;

    protected OrderItemInfo() {
    }

    public OrderItemInfo(
            String orderNumber,
            String sku,
            String productId,
            int quantity,
            int fulfilledQuantity,
            long clientId
    ) {
        this.orderNumber = orderNumber;
        this.sku = sku;
        this.productId = productId;
        this.quantity = quantity;
        this.fulfilledQuantity = fulfilledQuantity;
        this.clientId = clientId;
    }

    public long getId() {
        return id;
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

    public long getClientId() {
        return clientId;
    }

    public void increaseFulfilledQuantity(int quantity) {
        this.fulfilledQuantity += quantity;
    }

    public void attachTo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
