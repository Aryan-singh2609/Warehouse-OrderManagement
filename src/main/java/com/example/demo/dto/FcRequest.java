package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FcRequest", description = "Payload for creating or updating a fulfillment center.")
public class
FcRequest {

    @Schema(description = "Warehouse identifier.", example = "WH-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Warehouse ID is required")
    private String warehouseId;

    @Schema(description = "Fulfillment center identifier.", example = "FC-BLR-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "FC ID is required")
    private String fcId;

    @Schema(description = "Human-readable location name.", example = "Bengaluru South Hub", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location is required")
    private String location;

    @Schema(description = "Latitude coordinate.", example = "12.9716", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Latitude is required")
    private Double latitude;

    @Schema(description = "Longitude coordinate.", example = "77.5946", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Longitude is required")
    private Double longitude;

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getFcId() {
        return fcId;
    }

    public void setFcId(String fcId) {
        this.fcId = fcId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
