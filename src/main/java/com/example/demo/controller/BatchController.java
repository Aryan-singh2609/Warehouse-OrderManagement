package com.example.demo.controller;

import com.example.demo.dto.BatchRequest;
import com.example.demo.dto.BatchResponse;
import com.example.demo.service.BatchService;
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
@RequestMapping("/data/batches")
@Tag(name = "Batches", description = "Batch creation, assignment, and picking operations.")
@SecurityRequirement(name = "sessionAuth")
public class BatchController extends SessionControllerSupport {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    @Operation(summary = "Create a batch", description = "Creates a picking batch from a list of order numbers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch created",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or order selection invalid",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse createBatch(@Valid @RequestBody BatchRequest request, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.createBatch(request, userId);
    }

    @GetMapping
    @Operation(summary = "List batches", description = "Returns all batches visible to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batches returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BatchResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public List<BatchResponse> getBatches(HttpSession session) {
        long userId = requireLogin(session);
        return batchService.getBatches(userId);
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get a batch", description = "Returns a batch with picker and order details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch returned",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse getBatch(@PathVariable long batchId, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.getBatch(batchId, userId);
    }

    @PutMapping("/{batchId}")
    @Operation(summary = "Update a batch", description = "Replaces the orders associated with an existing batch.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch updated",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or order selection invalid",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse updateBatch(@PathVariable long batchId, @Valid @RequestBody BatchRequest request, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.updateBatch(batchId, request, userId);
    }

    @PostMapping("/{batchId}/assign/{pickerId}")
    @Operation(summary = "Assign a batch to a picker", description = "Assigns the specified batch to the specified picker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch assigned",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch or picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Batch cannot be assigned in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse assignBatch(@PathVariable long batchId, @PathVariable long pickerId, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.assignBatch(batchId, pickerId, userId);
    }

    @PostMapping("/{batchId}/assign-self")
    @Operation(summary = "Self-assign a batch", description = "Assigns the batch to the authenticated picker or warehouse user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch self-assigned",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Batch cannot be assigned in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse assignBatchToSelf(@PathVariable long batchId, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.assignBatchToSelf(batchId, userId);
    }

    @PostMapping("/{batchId}/pick-self")
    @Operation(summary = "Mark a batch as picked", description = "Marks all items in the batch as picked by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch marked as picked",
                    content = @Content(schema = @Schema(implementation = BatchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Batch cannot be picked in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public BatchResponse markBatchPicked(@PathVariable long batchId, HttpSession session) {
        long userId = requireLogin(session);
        return batchService.markBatchPicked(batchId, userId);
    }

    @DeleteMapping("/{batchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a batch", description = "Deletes a batch and returns its orders to the available pool.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Batch deleted"),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Batch not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public void deleteBatch(@PathVariable long batchId, HttpSession session) {
        long userId = requireLogin(session);
        batchService.deleteBatch(batchId, userId);
    }
}
