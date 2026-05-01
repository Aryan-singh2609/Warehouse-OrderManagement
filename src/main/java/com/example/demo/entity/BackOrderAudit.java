package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "back_order_audit")
public class BackOrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long backOrderCount;

    @Column(nullable = false)
    private LocalDateTime auditedAt;

    protected BackOrderAudit() {
    }

    public BackOrderAudit(long backOrderCount, LocalDateTime auditedAt) {
        this.backOrderCount = backOrderCount;
        this.auditedAt = auditedAt;
    }

    public long getId() {
        return id;
    }

    public long getBackOrderCount() {
        return backOrderCount;
    }

    public LocalDateTime getAuditedAt() {
        return auditedAt;
    }
}
