package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String organisationName;

    @Column(length = 1024)
    private String organisationAddress;

    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    protected Client() {
    }

    public Client(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Client(String name, String organisationName, String organisationAddress, String email, String phone) {
        this.name = name;
        this.organisationName = organisationName;
        this.organisationAddress = organisationAddress;
        this.email = email;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getOrganisationAddress() {
        return organisationAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void update(String name, String organisationName, String organisationAddress, String email, String phone) {
        this.name = name;
        this.organisationName = organisationName;
        this.organisationAddress = organisationAddress;
        this.email = email;
        this.phone = phone;
    }
}
