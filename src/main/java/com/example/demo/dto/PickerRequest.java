package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PickerRequest", description = "Payload for creating or updating a picker.")
public class PickerRequest {

    @Schema(description = "Picker full name.", example = "Riya Sharma", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Picker email.", example = "riya.sharma@warehouse.local", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "Unique employee identifier.", example = "PK-1024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @Schema(description = "Picker login password. Required when creating a picker and optional when updating.", example = "PickerPass123!", format = "password", minLength = 8, nullable = true)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
