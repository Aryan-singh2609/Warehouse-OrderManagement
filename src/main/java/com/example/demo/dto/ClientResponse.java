package com.example.demo.dto;

import com.example.demo.entity.Client;
import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("ALL")
@Schema(name = "ClientResponse", description = "Client organization returned by the API.")
public class ClientResponse {

    @Schema(description = "Internal database id.", example = "10")
    private long id;
    @Schema(description = "Primary contact name.", example = "Aarav Shah")
    private String name;
    @Schema(description = "Organization name.", example = "Northwind Retail")
    private String organisationName;
    @Schema(description = "Organization address.", example = "221B Baker Street, London")
    private String organisationAddress;
    @Schema(description = "Primary contact email.", example = "ops@northwind.example")
    private String email;
    @Schema(description = "Primary contact phone number.", example = "+44-20-5555-0101")
    private String phone;

    public ClientResponse(
            long id,
            String name,
            String organisationName,
            String organisationAddress,
            String email,
            String phone
    ) {
        this.id = id;
        this.name = name;
        this.organisationName = organisationName;
        this.organisationAddress = organisationAddress;
        this.email = email;
        this.phone = phone;
    }

    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getOrganisationName(),
                client.getOrganisationAddress(),
                client.getEmail(),
                client.getPhone()
        );
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
}
