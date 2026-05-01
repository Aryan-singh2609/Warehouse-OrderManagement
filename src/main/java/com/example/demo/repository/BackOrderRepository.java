package com.example.demo.repository;

import com.example.demo.entity.BackOrder;
import com.example.demo.entity.OrderInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackOrderRepository extends JpaRepository<BackOrder, Long> {

    List<BackOrder> findAllByOrderByCreatedAtAsc();

    List<BackOrder> findAllByOrderInfo_OrderNumberIgnoreCase(String orderNumber);

    List<BackOrder> findAllByOrderInfo(OrderInfo orderInfo);

    void deleteByOrderInfo(OrderInfo orderInfo);
}
