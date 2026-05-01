package com.example.demo.controller;

import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.PackOrderRequest;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
@Tag(name = "Packing", description = "Packing operations including box allocation and order packing.")
@SecurityRequirement(name = "sessionAuth")
public class PackingController extends SessionControllerSupport {

    private final OrderService orderService;

    public PackingController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/{orderNumber}/pack")
    @Operation(summary = "Pack an order", description = "Allocates a box based on weight and transitions the order to PACKED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order packed",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or the order cannot be packed",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse packOrder(
            @PathVariable String orderNumber,
            @Valid @RequestBody PackOrderRequest request,
            HttpSession session
    ) {
        long userId = requireLogin(session);
        return orderService.packOrder(orderNumber, request.getWeight(), userId);
    }
}
