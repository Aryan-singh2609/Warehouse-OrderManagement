package com.example.demo.repository;

import com.example.demo.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByNameAsc();

    Optional<Product> findByNameIgnoreCase(String name);

    Optional<Product> findByProductIdIgnoreCase(String productId);

    Optional<Product> findByProductIdIgnoreCaseAndSkuIgnoreCaseAndClient_IdAndFcInfo_FcIdIgnoreCase(
            String productId,
            String sku,
            long clientId,
            String fcId
    );

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsByClientId(long clientId);

    boolean existsByFcInfo_Id(long fcInfoId);
}
