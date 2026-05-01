package com.example.demo.controller;

import com.example.demo.dto.PickerRequest;
import com.example.demo.dto.PickerResponse;
import com.example.demo.service.PickerService;
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
@RequestMapping("/data/pickers")
@Tag(name = "Pickers", description = "Picker management and lookup operations.")
@SecurityRequirement(name = "sessionAuth")
public class PickerController extends SessionControllerSupport {

    private final PickerService pickerService;

    public PickerController(PickerService pickerService) {
        this.pickerService = pickerService;
    }

    @PostMapping
    @Operation(summary = "Create a picker", description = "Creates a picker record. Requires ADMIN or SUPER_ADMIN session privileges.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Picker created",
                    content = @Content(schema = @Schema(implementation = PickerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public PickerResponse createPicker(@Valid @RequestBody PickerRequest request, HttpSession session) {
        requireUserManager(session);
        return pickerService.createPicker(request);
    }

    @GetMapping
    @Operation(summary = "List pickers", description = "Returns all pickers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pickers returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PickerResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public List<PickerResponse> getPickers(HttpSession session) {
        requireLogin(session);
        return pickerService.getPickers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a picker", description = "Returns a picker by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Picker returned",
                    content = @Content(schema = @Schema(implementation = PickerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public PickerResponse getPicker(@PathVariable long id, HttpSession session) {
        requireLogin(session);
        return pickerService.getPicker(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a picker", description = "Updates a picker record. Requires ADMIN or SUPER_ADMIN session privileges.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Picker updated",
                    content = @Content(schema = @Schema(implementation = PickerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public PickerResponse updatePicker(@PathVariable long id, @Valid @RequestBody PickerRequest request, HttpSession session) {
        requireUserManager(session);
        return pickerService.updatePicker(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a picker", description = "Deletes a picker by id. Requires ADMIN or SUPER_ADMIN session privileges.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Picker deleted"),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin access required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public void deletePicker(@PathVariable long id, HttpSession session) {
        requireUserManager(session);
        pickerService.deletePicker(id);
    }
}
