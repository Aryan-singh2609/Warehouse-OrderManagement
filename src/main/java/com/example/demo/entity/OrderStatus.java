package com.example.demo.entity;

public enum OrderStatus {
    CREATED,
    BACK_ORDER,
    ASSIGNED_FOR_PICKING,
    PICKED,
    PACKING,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
