package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        long actorId = userRepository.save(new User(
                "Picker User",
                "picker@example.com",
                "hashed-password",
                Role.ASSOCIATE
        )).getId();

        PickerRequest pickerRequest = new PickerRequest();
        pickerRequest.setName("Picker User");
        pickerRequest.setEmail("picker@example.com");
        pickerRequest.setEmployeeId("EMP-1");
        pickerService.createPicker(pickerRequest);

        Client client = clientRepository.save(new Client(
                "Client One",
                "Org One",
                "Address One",
                "client-self@example.com",
                "9999999999"
        ));
        fcInfoRepository.save(new FcInfo("WH-SELF", "FC-SELF", "Bengaluru", 12.9716, 77.5946));
        productRepository.save(new Product(
                "Self Assign Product",
                "PROD-SELF",
                "SKU-SELF",
                client,
                fcInfoRepository.findByFcIdIgnoreCase("FC-SELF").orElseThrow(),
                "Self assignment product",
                BigDecimal.TEN,
                5
        ));

        OrderResponse createdOrder = orderService.createOrder(orderRequest(client.getId()), actorId);

        OrderResponse response = orderService.assignOrderToSelf(createdOrder.getOrderNumber(), actorId);

        assertEquals(OrderStatus.ASSIGNED_FOR_PICKING, response.getStatus());
        assertEquals("Picker User", response.getPickerName());
        assertEquals(createdOrder.getOrderNumber(), response.getOrderNumber());
        assertTrue(createdOrder.getOrderNumber().startsWith("ORDID-"));
    }

    private OrderRequest orderRequest(long clientId) {
        OrderItemRequest item = new OrderItemRequest();
        item.setClientId(clientId);
        item.setProductId("PROD-SELF");
        item.setSku("SKU-SELF");
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
