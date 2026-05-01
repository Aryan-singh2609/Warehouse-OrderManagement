package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Client;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final FcInfoRepository fcInfoRepository;
    private final UserRepository userRepository;

    public ProductService(
            ProductRepository productRepository,
            ClientRepository clientRepository,
            FcInfoRepository fcInfoRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.fcInfoRepository = fcInfoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, long actorId) {
        requireProductManager(actorId);
        validateUniqueFields(request, null);

        try {
            Product product = productRepository.save(new Product(
                    request.getName(),
                    request.getProductId(),
                    request.getSku(),
                    getClient(request.getClientId()),
                    getFc(request.getFcId()),
                    request.getDescription(),
                    request.getPrice(),
                    request.getQuantity()
            ));
            return ProductResponse.from(product);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product could not be created");
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(long actorId) {
        requireProductManager(actorId);
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getInventorySnapshot(long actorId) {
        requireProductManager(actorId);
        return productRepository.findAllByOrderByNameAsc()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(long productId, ProductRequest request, long actorId) {
        requireProductManager(actorId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        validateUniqueFields(request, productId);
        product.update(
                request.getName(),
                request.getProductId(),
                request.getSku(),
                getClient(request.getClientId()),
                getFc(request.getFcId()),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity()
        );

        try {
            return ProductResponse.from(productRepository.save(product));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product could not be updated");
        }
    }

    @Transactional
    public void deleteProduct(long productId, long actorId) {
        requireProductManager(actorId);

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        productRepository.deleteById(productId);
    }

    private void validateUniqueFields(ProductRequest request, Long productId) {
        productRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> productId == null || existing.getId() != productId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is already registered");
                });

        productRepository.findByProductIdIgnoreCase(request.getProductId())
                .filter(existing -> productId == null || existing.getId() != productId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID is already registered");
                });

        productRepository.findBySkuIgnoreCase(request.getSku())
                .filter(existing -> productId == null || existing.getId() != productId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU is already registered");
                });
    }

    private Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    private FcInfo getFc(String fcId) {
        return fcInfoRepository.findByFcIdIgnoreCase(fcId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FC not found"));
    }

    private User requireProductManager(long actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required"));

        if (actor.getRole() != Role.ASSOCIATE && actor.getRole() != Role.ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product management is not allowed");
        }

        return actor;
    }
}
