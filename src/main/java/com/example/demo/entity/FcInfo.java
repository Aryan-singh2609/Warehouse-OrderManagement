package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouse_info")
public class FcInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String warehouseId;

    @Column(nullable = false, unique = true)
    private String fcId;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @OneToMany(mappedBy = "fcInfo")
    private List<Product> products = new ArrayList<>();

    protected FcInfo() {
    }

    public FcInfo(String warehouseId, String fcId, String location, double latitude, double longitude) {
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public List<Product> getProducts() {
        return products;
    }

    public void update(String warehouseId, String fcId, String location, double latitude, double longitude) {
        this.warehouseId = warehouseId;
        this.fcId = fcId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
