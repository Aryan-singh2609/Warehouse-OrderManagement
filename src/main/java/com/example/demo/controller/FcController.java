package com.example.demo.controller;

import com.example.demo.dto.FcRequest;
import com.example.demo.dto.FcResponse;
import com.example.demo.service.FcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data/fcs")
@Tag(name = "Fulfillment Centers", description = "Fulfillment center and warehouse location operations.")
@SecurityRequirement(name = "sessionAuth")
public class FcController extends SessionControllerSupport {

    private final FcService fcService;

    public FcController(FcService fcService) {
        this.fcService = fcService;
    }

    @PostMapping
    @Operation(summary = "Create a fulfillment center", description = "Creates a fulfillment center record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fulfillment center created",
                    content = @Content(schema = @Schema(implementation = FcResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public FcResponse createFc(@Valid @RequestBody FcRequest request, HttpSession session) {
        long userId = requireLogin(session);
        return fcService.createFc(request, userId);
    }

    @GetMapping
    @Operation(summary = "List fulfillment centers", description = "Returns all fulfillment centers visible to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fulfillment centers returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FcResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public List<FcResponse> getFcs(HttpSession session) {
        long userId = requireLogin(session);
        return fcService.getFcs(userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a fulfillment center", description = "Updates the warehouse, location, or coordinates for a fulfillment center.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fulfillment center updated",
                    content = @Content(schema = @Schema(implementation = FcResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fulfillment center not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public FcResponse updateFc(@PathVariable long id, @Valid @RequestBody FcRequest request, HttpSession session) {
        long userId = requireLogin(session);
        return fcService.updateFc(id, request, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a fulfillment center", description = "Deletes a fulfillment center by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fulfillment center deleted"),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fulfillment center not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public void deleteFc(@PathVariable long id, HttpSession session) {
        long userId = requireLogin(session);
        fcService.deleteFc(id, userId);
    }
}
