package com.example.demo.controller;

import com.example.demo.dto.BackOrderAuditResponse;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.OrderStatusUpdateRequest;
import com.example.demo.service.OrderService;
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
@RequestMapping("/data")
@Tag(name = "Orders", description = "Order lifecycle, assignment, shipping, and audit operations.")
@SecurityRequirement(name = "sessionAuth")
public class OrderController extends SessionControllerSupport {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @Operation(summary = "Create an order", description = "Creates a new order with one or more line items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or business rule rejected the request",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request, HttpSession session) {
        long userId = requireLogin(session);
        return orderService.createOrder(request, userId);
    }

    @GetMapping("/orders")
    @Operation(summary = "List orders", description = "Returns all orders visible to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public List<OrderResponse> getOrders(HttpSession session) {
        long userId = requireLogin(session);
        return orderService.getOrders(userId);
    }

    @GetMapping("/back-order-audits")
    @Operation(summary = "List back-order audits", description = "Returns the historical audit entries for back-ordered inventory.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit history returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BackOrderAuditResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public List<BackOrderAuditResponse> getBackOrderAudits(HttpSession session) {
        long userId = requireLogin(session);
        return orderService.getBackOrderAudits(userId);
    }

    @PutMapping("/orders/{orderNumber}/status")
    @Operation(summary = "Update order status", description = "Manually changes the status of an order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or status transition is invalid",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse updateOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            HttpSession session
    ) {
        long userId = requireLogin(session);
        return orderService.updateOrderStatus(orderNumber, request.getStatus(), userId);
    }

    @PostMapping("/orders/{orderNumber}/assign/{pickerId}")
    @Operation(summary = "Assign an order", description = "Assigns the specified order to the specified picker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order assigned",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order or picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be assigned in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse assignOrder(@PathVariable String orderNumber, @PathVariable long pickerId, HttpSession session) {
        requireLogin(session);
        return orderService.assignOrder(orderNumber, pickerId);
    }

    @PostMapping("/orders/{orderNumber}/assign-self")
    @Operation(summary = "Self-assign an order", description = "Assigns the order to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order self-assigned",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be assigned in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse assignOrderToSelf(@PathVariable String orderNumber, HttpSession session) {
        long userId = requireLogin(session);
        return orderService.assignOrderToSelf(orderNumber, userId);
    }

    @PostMapping("/orders/{orderNumber}/pick/{pickerId}")
    @Operation(summary = "Mark an order as picked", description = "Marks the order as picked by the specified picker.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as picked",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order or picker not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be picked in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse markOrderPicked(@PathVariable String orderNumber, @PathVariable long pickerId, HttpSession session) {
        requireLogin(session);
        return orderService.markOrderPicked(orderNumber, pickerId);
    }

    @PostMapping("/orders/{orderNumber}/ship")
    @Operation(summary = "Mark an order as shipped", description = "Transitions the order from PACKED to SHIPPED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as shipped",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be shipped in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse markOrderShipped(@PathVariable String orderNumber, HttpSession session) {
        long userId = requireLogin(session);
        return orderService.markOrderShipped(orderNumber, userId);
    }

    @PostMapping("/orders/{orderNumber}/deliver")
    @Operation(summary = "Mark an order as delivered", description = "Transitions the order to DELIVERED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be delivered in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse markOrderDelivered(@PathVariable String orderNumber, HttpSession session) {
        long userId = requireLogin(session);
        return orderService.markOrderDelivered(orderNumber, userId);
    }

    @PostMapping("/orders/{orderNumber}/cancel")
    @Operation(summary = "Cancel an order", description = "Transitions the order to CANCELLED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled in its current state",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public OrderResponse markOrderCancelled(@PathVariable String orderNumber, HttpSession session) {
        long userId = requireLogin(session);
        return orderService.markOrderCancelled(orderNumber, userId);
    }

    @DeleteMapping("/orders/{orderNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an order", description = "Deletes an order by order number.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "401", description = "Login required",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    public void deleteOrder(@PathVariable String orderNumber, HttpSession session) {
        long userId = requireLogin(session);
        orderService.deleteOrder(orderNumber, userId);
    }
}
