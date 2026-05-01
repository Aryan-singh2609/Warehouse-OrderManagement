package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Client;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private FcService fcService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FcInfoRepository fcInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createProductRequiresAndPersistsFcMapping() {
        long actorId = createActor(Role.ADMIN);
        Client client = clientRepository.save(new Client(
                "Client Product",
                "Org Product",
                "Address Product",
                "client-product@example.com",
                "9999999999"
        ));
        fcInfoRepository.save(new FcInfo("WH-PROD", "FC-PROD", "Hyderabad", 17.3850, 78.4867));

        ProductRequest request = new ProductRequest();
        request.setName("Mapped Product");
        request.setProductId("PROD-MAP");
        request.setSku("SKU-MAP");
        request.setClientId(client.getId());
        request.setFcId("FC-PROD");
        request.setDescription("Product with FC mapping");
        request.setPrice(BigDecimal.valueOf(99.99));
        request.setQuantity(25);

        ProductResponse response = productService.createProduct(request, actorId);

        assertEquals("FC-PROD", response.getFcId());
        assertEquals("Hyderabad", response.getFcLocation());
        assertEquals("FC-PROD", productRepository.findById(response.getId()).orElseThrow().getFcInfo().getFcId());
    }

    @Test
    void deleteFcFailsWhenProductsAreAssigned() {
        long actorId = createActor(Role.ADMIN);
        Client client = clientRepository.save(new Client(
                "Client FC",
                "Org FC",
                "Address FC",
                "client-fc@example.com",
                "9999999999"
        ));
        FcInfo fcInfo = fcInfoRepository.save(new FcInfo("WH-LOCK", "FC-LOCK", "Pune", 18.5204, 73.8567));
        productRepository.save(new com.example.demo.entity.Product(
                "Locked Product",
                "PROD-LOCK",
                "SKU-LOCK",
                client,
                fcInfo,
                "Keeps FC in use",
                BigDecimal.TEN,
                4
        ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> fcService.deleteFc(fcInfo.getId(), actorId)
        );

        assertEquals("400 BAD_REQUEST \"FC cannot be deleted because products are assigned to it\"", exception.getMessage());
    }

    private long createActor(Role role) {
        return userRepository.save(new User(
                "Actor " + role.name(),
                "product-actor-" + role.name().toLowerCase() + "@example.com",
                "hashed-password",
                role
        )).getId();
    }
}
