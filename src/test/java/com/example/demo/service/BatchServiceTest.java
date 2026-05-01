package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.dto.BatchRequest;
import com.example.demo.dto.BatchResponse;
import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.PickerRequest;
import com.example.demo.entity.BoxCategory;
import com.example.demo.entity.BatchStatus;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BatchServiceTest {

    @Autowired
    private BatchService batchService;

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
    void createBatchHidesOrdersFromIndividualOperations() {
        long actorId = createActor("manager-batch-1@example.com", Role.ASSOCIATE);
        Client client = seedClient("client-batch-1@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-B1", "SKU-B1", 10);
        seedProduct(client, "PROD-B2", "SKU-B2", 10);

        OrderResponse firstOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-B1", "SKU-B1"), actorId);
        OrderResponse secondOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-B2", "SKU-B2"), actorId);

        BatchResponse response = batchService.createBatch(batchRequest(firstOrder.getOrderNumber(), secondOrder.getOrderNumber()), actorId);

        assertEquals(BatchStatus.UNASSIGNED, response.getStatus());
        assertEquals(2, response.getOrderCount());
        assertEquals(Set.of(firstOrder.getOrderNumber(), secondOrder.getOrderNumber()), response.getOrders().stream()
                .map(order -> order.getOrderNumber())
                .collect(java.util.stream.Collectors.toSet()));
        assertTrue(orderService.getOrders(actorId).isEmpty());
    }

    @Test
    void updateBatchReplacesReservedOrders() {
        long actorId = createActor("manager-batch-2@example.com", Role.ASSOCIATE);
        Client client = seedClient("client-batch-2@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-U1", "SKU-U1", 10);
        seedProduct(client, "PROD-U2", "SKU-U2", 10);
        seedProduct(client, "PROD-U3", "SKU-U3", 10);

        OrderResponse firstOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-U1", "SKU-U1"), actorId);
        OrderResponse secondOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-U2", "SKU-U2"), actorId);
        OrderResponse thirdOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-U3", "SKU-U3"), actorId);

        BatchResponse created = batchService.createBatch(batchRequest(firstOrder.getOrderNumber(), secondOrder.getOrderNumber()), actorId);
        BatchResponse updated = batchService.updateBatch(created.getBatchId(), batchRequest(secondOrder.getOrderNumber(), thirdOrder.getOrderNumber()), actorId);

        assertEquals(2, updated.getOrderCount());
        assertEquals(Set.of(secondOrder.getOrderNumber(), thirdOrder.getOrderNumber()), updated.getOrders().stream()
                .map(order -> order.getOrderNumber())
                .collect(java.util.stream.Collectors.toSet()));

        List<OrderResponse> availableOrders = orderService.getOrders(actorId);
        assertEquals(1, availableOrders.size());
        assertEquals(firstOrder.getOrderNumber(), availableOrders.getFirst().getOrderNumber());
    }

    @Test
    void pickerCanAssignBatchToSelfAndMarkItPicked() {
        long managerId = createActor("manager-batch-3@example.com", Role.ASSOCIATE);
        long pickerActorId = createActor("picker-batch@example.com", Role.ASSOCIATE);
        PickerRequest pickerRequest = new PickerRequest();
        pickerRequest.setName("Batch Picker");
        pickerRequest.setEmail("picker-batch@example.com");
        pickerRequest.setEmployeeId("EMP-BATCH");
        pickerService.createPicker(pickerRequest);

        Client client = seedClient("client-batch-3@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-P1", "SKU-P1", 10);
        seedProduct(client, "PROD-P2", "SKU-P2", 10);

        OrderResponse firstOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-P1", "SKU-P1"), managerId);
        OrderResponse secondOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-P2", "SKU-P2"), managerId);

        BatchResponse created = batchService.createBatch(batchRequest(firstOrder.getOrderNumber(), secondOrder.getOrderNumber()), managerId);
        BatchResponse assigned = batchService.assignBatchToSelf(created.getBatchId(), pickerActorId);

        assertEquals(BatchStatus.ASSIGNED, assigned.getStatus());
        assertEquals("Batch Picker", assigned.getPickerName());
        assertTrue(assigned.getOrders().stream().allMatch(order -> order.getOrderStatus() == OrderStatus.ASSIGNED_FOR_PICKING));

        BatchResponse picked = batchService.markBatchPicked(created.getBatchId(), pickerActorId);
        assertEquals(BatchStatus.PICKED, picked.getStatus());
        assertTrue(picked.getOrders().stream().allMatch(order -> order.getOrderStatus() == OrderStatus.PICKED));

        List<OrderResponse> availableOrders = orderService.getOrders(managerId);
        assertTrue(availableOrders.isEmpty());
    }

    @Test
    void deletingAssignedBatchReleasesOrdersForNormalPicking() {
        long managerId = createActor("manager-batch-4@example.com", Role.ADMIN);
        long pickerActorId = createActor("picker-release@example.com", Role.ASSOCIATE);
        PickerRequest pickerRequest = new PickerRequest();
        pickerRequest.setName("Release Picker");
        pickerRequest.setEmail("picker-release@example.com");
        pickerRequest.setEmployeeId("EMP-REL");
        long pickerId = pickerService.createPicker(pickerRequest).getId();

        Client client = seedClient("client-batch-4@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-R1", "SKU-R1", 10);

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-R1", "SKU-R1"), managerId);
        BatchResponse created = batchService.createBatch(batchRequest(createdOrder.getOrderNumber()), managerId);
        batchService.assignBatch(created.getBatchId(), pickerId, pickerActorId);

        batchService.deleteBatch(created.getBatchId(), managerId);

        List<OrderResponse> availableOrders = orderService.getOrders(managerId);
        assertEquals(1, availableOrders.size());
        assertEquals(createdOrder.getOrderNumber(), availableOrders.getFirst().getOrderNumber());
        assertEquals(OrderStatus.CREATED, availableOrders.getFirst().getStatus());
        assertNull(availableOrders.getFirst().getPickerId());
    }

    @Test
    void backOrderOrdersCannotBeAddedToBatch() {
        long actorId = createActor("manager-batch-5@example.com", Role.ASSOCIATE);
        Client client = seedClient("client-batch-5@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-BO1", "SKU-BO1", 0);

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-BO1", "SKU-BO1"), actorId);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> batchService.createBatch(batchRequest(createdOrder.getOrderNumber()), actorId)
        );

        assertEquals("400 BAD_REQUEST \"Orders in BACK_ORDER status cannot be added to a batch\"", exception.getMessage());
    }

    @Test
    void batchStatusTracksIndividualOrderLifecycleProgress() {
        long managerId = createActor("manager-batch-6@example.com", Role.ASSOCIATE);
        PickerRequest pickerRequest = new PickerRequest();
        pickerRequest.setName("Lifecycle Picker");
        pickerRequest.setEmail("picker-lifecycle@example.com");
        pickerRequest.setEmployeeId("EMP-LIFE");
        long pickerId = pickerService.createPicker(pickerRequest).getId();

        Client client = seedClient("client-batch-6@example.com");
        seedWarehouse();
        seedProduct(client, "PROD-L1", "SKU-L1", 10);
        seedProduct(client, "PROD-L2", "SKU-L2", 10);

        OrderResponse firstOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-L1", "SKU-L1"), managerId);
        OrderResponse secondOrder = orderService.createOrder(orderRequest(client.getId(), "PROD-L2", "SKU-L2"), managerId);

        BatchResponse created = batchService.createBatch(batchRequest(firstOrder.getOrderNumber(), secondOrder.getOrderNumber()), managerId);
        BatchResponse assigned = batchService.assignBatch(created.getBatchId(), pickerId, managerId);
        assertEquals(BatchStatus.ASSIGNED, assigned.getStatus());

        orderService.updateOrderStatus(firstOrder.getOrderNumber(), OrderStatus.PICKED, managerId);
        assertEquals(BatchStatus.ASSIGNED, batchService.getBatch(created.getBatchId(), managerId).getStatus());

        orderService.updateOrderStatus(secondOrder.getOrderNumber(), OrderStatus.PICKED, managerId);
        assertEquals(BatchStatus.PICKED, batchService.getBatch(created.getBatchId(), managerId).getStatus());

        OrderResponse packed = orderService.packOrder(firstOrder.getOrderNumber(), BigDecimal.valueOf(15), managerId);
        assertEquals(OrderStatus.PACKED, packed.getStatus());
        assertEquals(BoxCategory.PARCEL_BOX, packed.getBoxCategory());
        assertEquals(BatchStatus.PICKED, batchService.getBatch(created.getBatchId(), managerId).getStatus());

        OrderResponse secondPacked = orderService.packOrder(secondOrder.getOrderNumber(), BigDecimal.valueOf(75), managerId);
        assertEquals(OrderStatus.PACKED, secondPacked.getStatus());
        assertEquals(BoxCategory.MEDIUM_BOX, secondPacked.getBoxCategory());
        assertEquals(BatchStatus.PICKED, batchService.getBatch(created.getBatchId(), managerId).getStatus());

        orderService.updateOrderStatus(firstOrder.getOrderNumber(), OrderStatus.SHIPPED, managerId);
        assertEquals(BatchStatus.PICKED, batchService.getBatch(created.getBatchId(), managerId).getStatus());

        orderService.updateOrderStatus(secondOrder.getOrderNumber(), OrderStatus.SHIPPED, managerId);
        assertEquals(BatchStatus.SHIPPED, batchService.getBatch(created.getBatchId(), managerId).getStatus());
    }

    private long createActor(String email, Role role) {
        return userRepository.save(new User(
                "Actor " + role.name(),
                email,
                "hashed-password",
                role
        )).getId();
    }

    private Client seedClient(String email) {
        return clientRepository.save(new Client(
                "Client " + email,
                "Org " + email,
                "Address " + email,
                email,
                "9999999999"
        ));
    }

    private void seedWarehouse() {
        if (fcInfoRepository.findByFcIdIgnoreCase("FC-BATCH").isEmpty()) {
            fcInfoRepository.save(new FcInfo("WH-BATCH", "FC-BATCH", "Bengaluru", 12.9716, 77.5946));
        }
    }

    private void seedProduct(Client client, String productId, String sku, int quantity) {
        productRepository.save(new Product(
                "Product " + productId,
                productId,
                sku,
                client,
                fcInfoRepository.findByFcIdIgnoreCase("FC-BATCH").orElseThrow(),
                "Batch product",
                BigDecimal.TEN,
                quantity
        ));
    }

    private OrderRequest orderRequest(long clientId, String productId, String sku) {
        OrderItemRequest item = new OrderItemRequest();
        item.setClientId(clientId);
        item.setProductId(productId);
        item.setSku(sku);
        item.setQuantity(1);

        OrderRequest request = new OrderRequest();
        request.setClientId(clientId);
        request.setFcId("FC-BATCH");
        request.setBillToAddress("Billing Address");
        request.setShipToAddress("Shipping Address");
        request.setItems(List.of(item));
        return request;
    }

    private BatchRequest batchRequest(String... orderNumbers) {
        BatchRequest request = new BatchRequest();
        request.setOrderNumbers(List.of(orderNumbers));
        return request;
    }
}
