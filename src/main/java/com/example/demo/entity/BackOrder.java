package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "back_order")
public class BackOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_info_id", nullable = false)
    private OrderInfo orderInfo;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private long clientId;

    @Column(nullable = false)
    private int backOrderedQuantity;

    @Column(nullable = false, length = 1024)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastAuditedAt;

    protected BackOrder() {
    }

    public BackOrder(
            OrderInfo orderInfo,
            String sku,
            String productId,
            long clientId,
            int backOrderedQuantity,
            String reason
    ) {
        this.orderInfo = orderInfo;
        this.sku = sku;
        this.productId = productId;
        this.clientId = clientId;
        this.backOrderedQuantity = backOrderedQuantity;
        this.reason = reason;
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

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public String getSku() {
        return sku;
    }

    public String getProductId() {
        return productId;
    }

    public long getClientId() {
        return clientId;
    }

    public int getBackOrderedQuantity() {
        return backOrderedQuantity;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAuditedAt() {
        return lastAuditedAt;
    }

    public void attachTo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public void markAudited(LocalDateTime auditedAt) {
        this.lastAuditedAt = auditedAt;
    }
}
