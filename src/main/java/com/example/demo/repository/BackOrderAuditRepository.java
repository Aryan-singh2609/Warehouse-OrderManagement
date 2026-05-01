package com.example.demo.repository;

import com.example.demo.entity.BackOrderAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackOrderAuditRepository extends JpaRepository<BackOrderAudit, Long> {

    List<BackOrderAudit> findAllByOrderByAuditedAtDesc();
}
