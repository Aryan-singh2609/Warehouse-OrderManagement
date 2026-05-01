package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String productId;

    @Column(nullable = false, unique = true)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fc_info_id", nullable = false)
    private FcInfo fcInfo;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Min(0)
    @Column(nullable = false)
    private int quantity;

    protected Product() {
    }

    public Product(String name, String productId, String sku, Client client, FcInfo fcInfo, String description, BigDecimal price, int quantity) {
        this.name = name;
        this.productId = productId;
        this.sku = sku;
        this.client = client;
        this.fcInfo = fcInfo;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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

    public Client getClient() {
        return client;
    }

    public FcInfo getFcInfo() {
        return fcInfo;
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

    public void update(String name, String productId, String sku, Client client, FcInfo fcInfo, String description, BigDecimal price, int quantity) {
        this.name = name;
        this.productId = productId;
        this.sku = sku;
        this.client = client;
        this.fcInfo = fcInfo;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
}
