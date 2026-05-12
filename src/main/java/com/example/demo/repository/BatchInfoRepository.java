package com.example.demo.repository;

import com.example.demo.entity.BatchInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchInfoRepository extends JpaRepository<BatchInfo, Long> {

    List<BatchInfo> findAllByOrderByCreatedAtDesc();

    boolean existsByPicker_Id(long pickerId);
}
