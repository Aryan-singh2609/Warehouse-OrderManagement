package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "ProductRequest", description = "Payload for creating or updating a product.")
public class ProductRequest {

    @Schema(description = "Product display name.", example = "USB Barcode Scanner", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(description = "External or business product identifier.", example = "PRD-1001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product ID is required")
    private String productId;

    @Schema(description = "Stock keeping unit.", example = "SKU-USB-SCAN-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SKU is required")
    private String sku;

    @Schema(description = "Owning client id.", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Client is required")
    private Long clientId;

    @Schema(description = "Fulfillment center id where this product is stocked.", example = "FC-BLR-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "FC ID is required")
    private String fcId;

    @Schema(description = "Product description.", example = "Handheld USB barcode scanner for warehouse picking.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product description is required")
    private String description;

    @Schema(description = "Unit price.", example = "2499.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0")
    private BigDecimal price;

    @Schema(description = "Available inventory quantity.", example = "150", minimum = "0")
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getFcId() {
        return fcId;
    }

    public void setFcId(String fcId) {
        this.fcId = fcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
