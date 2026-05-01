package com.example.demo.dto;

import com.example.demo.entity.FcInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("ALL")
@Schema(name = "FcResponse", description = "Fulfillment center returned by the API.")
public class FcResponse {

    @Schema(description = "Internal database id.", example = "5")
    private long id;
    @Schema(description = "Warehouse identifier.", example = "WH-01")
    private String warehouseId;
    @Schema(description = "Fulfillment center identifier.", example = "FC-BLR-01")
    private String fcId;
    @Schema(description = "Human-readable location name.", example = "Bengaluru South Hub")
    private String location;
    @Schema(description = "Latitude coordinate.", example = "12.9716")
    private double latitude;
    @Schema(description = "Longitude coordinate.", example = "77.5946")
    private double longitude;

    public FcResponse(long id, String warehouseId, String fcId, String location, double latitude, double longitude) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static FcResponse from(FcInfo fcInfo) {
        return new FcResponse(
                fcInfo.getId(),
                fcInfo.getWarehouseId(),
                fcInfo.getFcId(),
                fcInfo.getLocation(),
                fcInfo.getLatitude(),
                fcInfo.getLongitude()
        );
    }

    public long getId() {
        return id;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getFcId() {
        return fcId;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
