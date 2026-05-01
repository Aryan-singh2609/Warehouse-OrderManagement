package com.example.demo.repository;

import com.example.demo.entity.OrderInfo;
import com.example.demo.entity.BatchStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {

    Optional<OrderInfo> findByOrderNumberIgnoreCase(String orderNumber);

    List<OrderInfo> findAllByOrderByCreatedAtDesc();

    @Query("""
            select o
            from OrderInfo o
            where not exists (
                select 1
                from BatchOrderInfo bo
                where bo.orderInfo = o
                and bo.batchInfo.status in :statuses
            )
            order by o.createdAt desc
            """)
    List<OrderInfo> findAllAvailableForIndividualOperations(@Param("statuses") List<BatchStatus> statuses);

    boolean existsByClientId(long clientId);
}
