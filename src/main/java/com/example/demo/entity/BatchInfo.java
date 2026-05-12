package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

@Entity
@Table(name = "batch_info")
public class BatchInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picker_id")
    private Picker picker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    @Column(nullable = false)
    private int orderCount;

    @OneToMany(mappedBy = "batchInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchOrderInfo> orders = new ArrayList<>();

    protected BatchInfo() {
    }

    public BatchInfo(BatchStatus status) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Picker getPicker() {
        return picker;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public List<BatchOrderInfo> getOrders() {
        return orders;
    }

    public void addOrder(BatchOrderInfo order) {
        orders.add(order);
        order.attachTo(this);
    }

    public void clearOrders() {
        orders.clear();
    }

    public void assignPicker(Picker picker) {
        this.picker = picker;
        this.status = BatchStatus.ASSIGNED;
    }

    public void markPicked() {
        this.status = BatchStatus.PICKED;
    }

    public void markFulfilled() {
        this.status = BatchStatus.FULFILLED;
    }

    public void updateStatus(BatchStatus status) {
        this.status = status;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
