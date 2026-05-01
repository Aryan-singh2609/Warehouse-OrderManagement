package com.example.demo.dto;

import com.example.demo.entity.BackOrderAudit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@SuppressWarnings("ALL")
@Schema(name = "BackOrderAuditResponse", description = "Audit record for back-ordered inventory.")
public class BackOrderAuditResponse {

    @Schema(description = "Internal database id.", example = "301")
    private long id;
    @Schema(description = "Total number of back-ordered units observed during the audit.", example = "14")
    private long backOrderCount;
    @Schema(description = "Timestamp when the audit entry was recorded.", example = "2026-04-29T08:00:00")
    private LocalDateTime auditedAt;

    public BackOrderAuditResponse(
            long id,
            long backOrderCount,
            LocalDateTime auditedAt
    ) {
        this.id = id;
        this.backOrderCount = backOrderCount;
        this.auditedAt = auditedAt;
    }

    public static BackOrderAuditResponse from(BackOrderAudit audit) {
        return new BackOrderAuditResponse(
                audit.getId(),
                audit.getBackOrderCount(),
                audit.getAuditedAt()
        );
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
