package com.example.demo.dto;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserResponse", description = "User profile returned by the API.")
public class UserResponse {

    @Schema(description = "Internal database id.", example = "1")
    private long id;
    @Schema(description = "Display name of the user.", example = "Jane Admin")
    private String name;
    @Schema(description = "Unique user email.", example = "jane.admin@warehouse.local")
    private String email;
    @Schema(description = "Application role assigned to the user.", example = "ADMIN")
    private Role role;

    public UserResponse(long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
