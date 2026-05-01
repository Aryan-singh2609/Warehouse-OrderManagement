package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("ALL")
@Schema(name = "ErrorResponse", description = "Standard error response payload.")
public class ErrorResponse {

    @Schema(description = "Human-readable error message.", example = "Login is required")
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
