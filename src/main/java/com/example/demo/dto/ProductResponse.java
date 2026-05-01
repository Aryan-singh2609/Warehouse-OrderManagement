package com.example.demo.dto;

import com.example.demo.entity.Product;
import com.example.demo.entity.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Schema(name = "ProductResponse", description = "Product returned by the API, including client details.")
public class ProductResponse {

    @Schema(description = "Internal database id.", example = "99")
    private long id;
    @Schema(description = "Product display name.", example = "USB Barcode Scanner")
    private String name;
    @Schema(description = "External or business product identifier.", example = "PRD-1001")
    private String productId;
    @Schema(description = "Stock keeping unit.", example = "SKU-USB-SCAN-01")
    private String sku;
    @Schema(description = "Owning client id. Zero means no client is currently assigned.", example = "10")
    private long clientId;
    @Schema(description = "Owning client name.", example = "Northwind Retail")
    private String clientName;
    @Schema(description = "Owning client email.", example = "ops@northwind.example", nullable = true)
    private String clientEmail;
    @Schema(description = "Owning client organization name.", example = "Northwind Retail", nullable = true)
    private String clientOrganisationName;
    @Schema(description = "Owning client organization address.", example = "221B Baker Street, London", nullable = true)
    private String clientOrganisationAddress;
    @Schema(description = "Owning client phone number.", example = "+44-20-5555-0101", nullable = true)
    private String clientPhone;
    @Schema(description = "Fulfillment center id where this product is stocked.", example = "FC-BLR-01")
    private String fcId;
    @Schema(description = "Fulfillment center location.", example = "Bengaluru South Hub")
    private String fcLocation;
    @Schema(description = "Product description.", example = "Handheld USB barcode scanner for warehouse picking.")
    private String description;
    @Schema(description = "Unit price.", example = "2499.99")
    private BigDecimal price;
    @Schema(description = "Available inventory quantity.", example = "150")
    private int quantity;

    public ProductResponse(
            long id,
            String name,
            String productId,
            String sku,
            long clientId,
            String clientName,
            String clientEmail,
            String clientOrganisationName,
            String clientOrganisationAddress,
            String clientPhone,
            String fcId,
            String fcLocation,
            String description,
            BigDecimal price,
            int quantity
    ) {
        this.id = id;
        this.name = name;
        this.productId = productId;
        this.sku = sku;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientOrganisationName = clientOrganisationName;
        this.clientOrganisationAddress = clientOrganisationAddress;
        this.clientPhone = clientPhone;
        this.fcId = fcId;
        this.fcLocation = fcLocation;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public static ProductResponse from(Product product) {
        Client client = null;
        Long clientId = null;
        String clientName = "Unassigned";
        String clientEmail = null;
        String clientOrganisationName = null;
        String clientOrganisationAddress = null;
        String clientPhone = null;
        String fcId = product.getFcInfo().getFcId();
        String fcLocation = product.getFcInfo().getLocation();

        try {
            client = product.getClient();
            if (client != null) {
                clientId = client.getId();
                clientName = client.getName();
                clientEmail = client.getEmail();
                clientOrganisationName = client.getOrganisationName();
                clientOrganisationAddress = client.getOrganisationAddress();
                clientPhone = client.getPhone();
            }
        } catch (EntityNotFoundException exception) {
            clientName = "Invalid client reference";
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getProductId(),
                product.getSku(),
                clientId == null ? 0 : clientId,
                clientName,
                clientEmail,
                clientOrganisationName,
                clientOrganisationAddress,
                clientPhone,
                fcId,
                fcLocation,
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientOrganisationName() {
        return clientOrganisationName;
    }

    public String getClientOrganisationAddress() {
        return clientOrganisationAddress;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getFcId() {
        return fcId;
    }

    public String getFcLocation() {
        return fcLocation;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
