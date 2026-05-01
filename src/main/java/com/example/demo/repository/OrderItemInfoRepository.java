package com.example.demo.repository;

import com.example.demo.entity.OrderItemInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemInfoRepository extends JpaRepository<OrderItemInfo, Long> {
}
