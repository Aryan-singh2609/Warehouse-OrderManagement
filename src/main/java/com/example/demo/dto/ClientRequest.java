package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ClientRequest", description = "Payload for creating or updating a client organization.")
public class ClientRequest {

    @Schema(description = "Primary contact name.", example = "Aarav Shah", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Organization name.", example = "Northwind Retail", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Organisation name is required")
    private String organisationName;

    @Schema(description = "Organization billing or registered address.", example = "221B Baker Street, London", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Organisation address is required")
    private String organisationAddress;

    @Schema(description = "Primary contact email.", example = "ops@northwind.example", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "Primary contact phone number.", example = "+44-20-5555-0101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone is required")
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(String organisationAddress) {
        this.organisationAddress = organisationAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
