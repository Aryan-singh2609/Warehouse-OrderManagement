package com.example.demo.repository;

import com.example.demo.entity.BatchInfo;
import com.example.demo.entity.BatchOrderInfo;
import com.example.demo.entity.BatchStatus;
import com.example.demo.entity.OrderInfo;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchOrderInfoRepository extends JpaRepository<BatchOrderInfo, Long> {

    boolean existsByOrderInfoAndBatchInfo_StatusIn(OrderInfo orderInfo, Collection<BatchStatus> statuses);

    List<BatchOrderInfo> findAllByOrderInfo(OrderInfo orderInfo);

    List<BatchOrderInfo> findAllByBatchInfoIdOrderByOrderNumberAscIdAsc(long batchInfoId);

    void deleteByBatchInfo(BatchInfo batchInfo);
}
