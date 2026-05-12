package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.PickerRequest;
import com.example.demo.entity.Client;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ShippingLabelService.ShippingLabelDocument;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class OrderSelfAssignmentTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PickerService pickerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FcInfoRepository fcInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void pickerCanAssignCreatedOrderToSelf() {
        long actorId = createManagerActor("manager-self@example.com");
        long pickerActorId = createPickerActor("picker@example.com", "EMP-1");
        Client client = seedClientAndInventory("client-self@example.com", 5);

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId()), actorId);

        OrderResponse response = orderService.assignOrderToSelf(createdOrder.getOrderNumber(), pickerActorId);

        assertEquals(OrderStatus.ASSIGNED_FOR_PICKING, response.getStatus());
        assertEquals("Picker User", response.getPickerName());
        assertEquals(createdOrder.getOrderNumber(), response.getOrderNumber());
        assertTrue(createdOrder.getOrderNumber().startsWith("ORDID-"));
    }

    @Test
    void pickerSeesOnlyAvailableUnassignedOrdersInQueue() {
        long managerActorId = createManagerActor("manager-queue@example.com");
        long pickerActorId = createPickerActor("picker-queue@example.com", "EMP-QUEUE");
        Client client = seedClientAndInventory("client-queue@example.com", 10);

        OrderResponse claimedOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);
        OrderResponse availableOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);

        orderService.assignOrderToSelf(claimedOrder.getOrderNumber(), pickerActorId);

        List<OrderResponse> pickerQueue = orderService.getOrders(pickerActorId);

        assertEquals(1, pickerQueue.size());
        assertEquals(availableOrder.getOrderNumber(), pickerQueue.getFirst().getOrderNumber());
        assertEquals(OrderStatus.CREATED, pickerQueue.getFirst().getStatus());
    }

    @Test
    void pickerCanViewAssignedAndPickedOrdersInHistory() {
        long managerActorId = createManagerActor("manager-history@example.com");
        long pickerActorId = createPickerActor("picker-history@example.com", "EMP-HISTORY");
        Client client = seedClientAndInventory("client-history@example.com", 10);

        OrderResponse assignedOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);
        OrderResponse pickedOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);

        orderService.assignOrderToSelf(assignedOrder.getOrderNumber(), pickerActorId);
        orderService.assignOrderToSelf(pickedOrder.getOrderNumber(), pickerActorId);
        orderService.markOrderPickedByActor(pickedOrder.getOrderNumber(), pickerActorId);

        List<OrderResponse> pickerHistory = orderService.getPickerOrderHistory(pickerActorId);

        assertEquals(2, pickerHistory.size());
        assertTrue(pickerHistory.stream().anyMatch(order ->
                order.getOrderNumber().equalsIgnoreCase(assignedOrder.getOrderNumber())
                        && order.getStatus() == OrderStatus.ASSIGNED_FOR_PICKING));
        assertTrue(pickerHistory.stream().anyMatch(order ->
                order.getOrderNumber().equalsIgnoreCase(pickedOrder.getOrderNumber())
                        && order.getStatus() == OrderStatus.PICKED
                        && order.getPickedAt() != null));
    }

    @Test
    void packingAnOrderGeneratesShippingLabelPdf() {
        long managerActorId = createManagerActor("manager-pack@example.com");
        long pickerActorId = createPickerActor("picker-pack@example.com", "EMP-PACK");
        Client client = seedClientAndInventory("client-pack@example.com", 10);

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);
        orderService.assignOrderToSelf(createdOrder.getOrderNumber(), pickerActorId);
        orderService.markOrderPickedByActor(createdOrder.getOrderNumber(), pickerActorId);

        OrderResponse packedOrder = orderService.packOrder(createdOrder.getOrderNumber(), BigDecimal.valueOf(12.5), managerActorId);
        ShippingLabelDocument shippingLabel = orderService.getShippingLabel(createdOrder.getOrderNumber(), managerActorId);

        assertEquals(OrderStatus.PACKED, packedOrder.getStatus());
        assertTrue(packedOrder.isShippingLabelAvailable());
        assertTrue(packedOrder.getShippingLabelDownloadUrl().endsWith("/shipping-label"));
        assertTrue(packedOrder.getShippingLabelGeneratedAt() != null);
        assertTrue(shippingLabel.fileName().endsWith(".pdf"));
        assertTrue(shippingLabel.content().length > 4);
        assertEquals("%PDF", new String(shippingLabel.content(), 0, 4, java.nio.charset.StandardCharsets.US_ASCII));
    }

    @Test
    void shippingLabelCannotBeDownloadedBeforePacking() {
        long managerActorId = createManagerActor("manager-no-label@example.com");
        Client client = seedClientAndInventory("client-no-label@example.com", 10);

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId()), managerActorId);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.getShippingLabel(createdOrder.getOrderNumber(), managerActorId)
        );

        assertEquals("404 NOT_FOUND \"Shipping label has not been generated for this order yet\"", exception.getMessage());
    }

    private long createManagerActor(String email) {
        return userRepository.save(new User(
                "Order Manager",
                email,
                "hashed-password",
                Role.ASSOCIATE
        )).getId();
    }

    private long createPickerActor(String email, String employeeId) {
        PickerRequest pickerRequest = new PickerRequest();
        pickerRequest.setName("Picker User");
        pickerRequest.setEmail(email);
        pickerRequest.setEmployeeId(employeeId);
        pickerRequest.setPassword("PickerPass123!");
        pickerService.createPicker(pickerRequest);
        return userRepository.findByEmailIgnoreCase(email).orElseThrow().getId();
    }

    private Client seedClientAndInventory(String email, int quantity) {
        Client client = clientRepository.save(new Client(
                "Client One",
                "Org One",
                "Address One",
                email,
                "9999999999"
        ));
        if (fcInfoRepository.findByFcIdIgnoreCase("FC-SELF").isEmpty()) {
            fcInfoRepository.save(new FcInfo("WH-SELF", "FC-SELF", "Bengaluru", 12.9716, 77.5946));
        }
        productRepository.save(new Product(
                "Self Assign Product " + email,
                "PROD-" + email,
                "SKU-" + email,
                client,
                fcInfoRepository.findByFcIdIgnoreCase("FC-SELF").orElseThrow(),
                "Self assignment product",
                BigDecimal.TEN,
                quantity
        ));
        return client;
    }

    private OrderRequest orderRequest(long clientId) {
        OrderItemRequest item = new OrderItemRequest();
        item.setClientId(clientId);
        Product product = productRepository.findAll().stream()
                .filter(entry -> entry.getClient().getId() == clientId)
                .findFirst()
                .orElseThrow();
        item.setProductId(product.getProductId());
        item.setSku(product.getSku());
        item.setQuantity(1);

        OrderRequest request = new OrderRequest();
        request.setClientId(clientId);
        request.setFcId("FC-SELF");
        request.setBillToAddress("Billing Address");
        request.setShipToAddress("Shipping Address");
        request.setItems(List.of(item));
        return request;
    }
}
