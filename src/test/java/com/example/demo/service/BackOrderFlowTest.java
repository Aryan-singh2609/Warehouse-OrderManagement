package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.BackOrder;
import com.example.demo.entity.BackOrderAudit;
import com.example.demo.entity.Client;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.BackOrderAuditRepository;
import com.example.demo.repository.BackOrderRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BackOrderFlowTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BackOrderAuditScheduler backOrderAuditScheduler;

    @Autowired
    private BackOrderRepository backOrderRepository;

    @Autowired
    private BackOrderAuditRepository backOrderAuditRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FcInfoRepository fcInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createOrderStoresBackOrderWhenInventoryIsUnavailable() {
        long actorId = createActor(Role.ASSOCIATE);
        Client client = clientRepository.save(new Client(
                "Client One",
                "Org One",
                "Address One",
                "client-one@example.com",
                "9999999999"
        ));
        fcInfoRepository.save(new FcInfo("WH-1", "FC-1", "Mumbai", 19.0760, 72.8777));
        productRepository.save(new Product(
                "Widget",
                "PROD-1",
                "SKU-1",
                client,
                fcInfoRepository.findByFcIdIgnoreCase("FC-1").orElseThrow(),
                "Test product",
                BigDecimal.TEN,
                2
        ));

        OrderResponse response = orderService.createOrder(orderRequest("FC-1", "PROD-1", "SKU-1", client.getId(), 5), actorId);

        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertTrue(response.getOrderNumber().startsWith("ORDID"));
        BackOrder backOrder = backOrderRepository.findAllByOrderInfo_OrderNumberIgnoreCase(response.getOrderNumber()).getFirst();
        assertEquals(response.getOrderNumber(), backOrder.getOrderInfo().getOrderNumber());
        assertTrue(backOrder.getReason().contains("Insufficient quantity"));
        assertEquals(1, backOrderRepository.findAllByOrderInfo_OrderNumberIgnoreCase(response.getOrderNumber()).size());
        assertEquals(3, backOrder.getBackOrderedQuantity());
        assertEquals(0, productRepository.findByProductIdIgnoreCase("PROD-1").orElseThrow().getQuantity());
        assertEquals(5, response.getItems().getFirst().getQuantity());
        assertEquals(2, response.getItems().getFirst().getFulfilledQuantity());
        assertEquals(3, response.getItems().getFirst().getBackOrderedQuantity());
    }

    @Test
    void schedulerCopiesBackOrdersIntoAuditTable() {
        long actorId = createActor(Role.ADMIN);
        Client client = clientRepository.save(new Client(
                "Client Two",
                "Org Two",
                "Address Two",
                "client-two@example.com",
                "8888888888"
        ));
        fcInfoRepository.save(new FcInfo("WH-2", "FC-2", "Delhi", 28.6139, 77.2090));
        productRepository.save(new Product(
                "Gadget",
                "PROD-2",
                "SKU-2",
                client,
                fcInfoRepository.findByFcIdIgnoreCase("FC-2").orElseThrow(),
                "Audit product",
                BigDecimal.ONE,
                0
        ));

        OrderResponse response = orderService.createOrder(orderRequest("FC-2", "PROD-2", "SKU-2", client.getId(), 1), actorId);
        assertTrue(response.getOrderNumber().startsWith("ORDID"));

        backOrderAuditScheduler.auditBackOrders();

        List<BackOrderAudit> audits = backOrderAuditRepository.findAllByOrderByAuditedAtDesc();
        assertFalse(audits.isEmpty());
        assertEquals(1, audits.getFirst().getBackOrderCount());
        assertNotNull(audits.getFirst().getAuditedAt());
    }

    private long createActor(Role role) {
        return userRepository.save(new User(
                "Actor " + role.name(),
                "actor-" + role.name().toLowerCase() + "@example.com",
                "hashed-password",
                role
        )).getId();
    }

    private OrderRequest orderRequest(
            String fcId,
            String productId,
            String sku,
            long clientId,
            int quantity
    ) {
        OrderItemRequest item = new OrderItemRequest();
        item.setClientId(clientId);
        item.setProductId(productId);
        item.setSku(sku);
        item.setQuantity(quantity);

        OrderRequest request = new OrderRequest();
        request.setClientId(clientId);
        request.setFcId(fcId);
        request.setBillToAddress("Billing Address");
        request.setShipToAddress("Shipping Address");
        request.setItems(List.of(item));
        return request;
    }
}
