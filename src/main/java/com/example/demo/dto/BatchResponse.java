package com.example.demo.dto;

import com.example.demo.entity.BatchInfo;
import com.example.demo.entity.BatchStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "BatchResponse", description = "Batch returned by the API.")
public class BatchResponse {

    @Schema(description = "Batch identifier.", example = "77")
    private long batchId;
    @Schema(description = "Timestamp when the batch was created.", example = "2026-04-29T11:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "Current batch status.", example = "ASSIGNED")
    private BatchStatus status;
    @Schema(description = "Number of orders in the batch.", example = "4")
    private int orderCount;
    @Schema(description = "Assigned picker id, if any.", example = "22", nullable = true)
    private Long pickerId;
    @Schema(description = "Assigned picker name, if any.", example = "Riya Sharma", nullable = true)
    private String pickerName;
    @Schema(description = "Assigned picker email, if any.", example = "riya.sharma@warehouse.local", nullable = true)
    private String pickerEmail;
    @Schema(description = "Assigned picker employee id, if any.", example = "PK-1024", nullable = true)
    private String pickerEmployeeId;
    @ArraySchema(schema = @Schema(implementation = BatchOrderResponse.class), arraySchema = @Schema(description = "Orders included in the batch."))
    private List<BatchOrderResponse> orders;

    public BatchResponse(
            long batchId,
            LocalDateTime createdAt,
            BatchStatus status,
            int orderCount,
            Long pickerId,
            String pickerName,
            String pickerEmail,
            String pickerEmployeeId,
            List<BatchOrderResponse> orders
    ) {
        this.batchId = batchId;
        this.createdAt = createdAt;
        this.status = status;
        this.orderCount = orderCount;
        this.pickerId = pickerId;
        this.pickerName = pickerName;
        this.pickerEmail = pickerEmail;
        this.pickerEmployeeId = pickerEmployeeId;
        this.orders = orders;
    }

    public static BatchResponse from(BatchInfo batchInfo) {
        return new BatchResponse(
                batchInfo.getId(),
                batchInfo.getCreatedAt(),
                batchInfo.getStatus(),
                batchInfo.getOrderCount(),
                batchInfo.getPicker() == null ? null : batchInfo.getPicker().getId(),
                batchInfo.getPicker() == null ? null : batchInfo.getPicker().getName(),
                batchInfo.getPicker() == null ? null : batchInfo.getPicker().getEmail(),
                batchInfo.getPicker() == null ? null : batchInfo.getPicker().getEmployeeId(),
                batchInfo.getOrders().stream().map(BatchOrderResponse::from).toList()
        );
    }

    public long getBatchId() {
        return batchId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public int getOrderCount() {
        return orderCount;
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

    public List<BatchOrderResponse> getOrders() {
        return orders;
    }
}
