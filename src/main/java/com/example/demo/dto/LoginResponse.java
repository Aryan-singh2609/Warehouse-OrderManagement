package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "LoginResponse", description = "Authenticated user and the user list returned after login.")
public class LoginResponse {

    @Schema(description = "Authenticated user profile.")
    private UserResponse user;

    @ArraySchema(schema = @Schema(implementation = UserResponse.class), arraySchema = @Schema(description = "Users returned as part of the login flow."))
    private List<UserResponse> users;

    public LoginResponse(UserResponse user, List<UserResponse> users) {
        this.user = user;
        this.users = users;
    }

    public UserResponse getUser() {
        return user;
    }

    public List<UserResponse> getUsers() {
        return users;
    }
}
