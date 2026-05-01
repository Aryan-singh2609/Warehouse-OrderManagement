package com.example.demo.repository;

import com.example.demo.entity.FcInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcInfoRepository extends JpaRepository<FcInfo, Long> {

    Optional<FcInfo> findByFcIdIgnoreCase(String fcId);
}
