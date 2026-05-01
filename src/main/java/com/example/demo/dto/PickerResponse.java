package com.example.demo.dto;

import com.example.demo.entity.Picker;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PickerResponse", description = "Picker returned by the API.")
public class PickerResponse {

    @Schema(description = "Internal database id.", example = "22")
    private long id;
    @Schema(description = "Picker full name.", example = "Riya Sharma")
    private String name;
    @Schema(description = "Picker email.", example = "riya.sharma@warehouse.local")
    private String email;
    @Schema(description = "Unique employee identifier.", example = "PK-1024")
    private String employeeId;

    public PickerResponse(long id, String name, String email, String employeeId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.employeeId = employeeId;
    }

    public static PickerResponse from(Picker picker) {
        return new PickerResponse(picker.getId(), picker.getName(), picker.getEmail(), picker.getEmployeeId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
