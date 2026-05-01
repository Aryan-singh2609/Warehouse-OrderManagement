package com.example.demo.service;

import com.example.demo.entity.BackOrderAudit;
import com.example.demo.repository.BackOrderAuditRepository;
import com.example.demo.repository.BackOrderRepository;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BackOrderAuditScheduler {

    private final BackOrderRepository backOrderRepository;
    private final BackOrderAuditRepository backOrderAuditRepository;

    public BackOrderAuditScheduler(
            BackOrderRepository backOrderRepository,
            BackOrderAuditRepository backOrderAuditRepository
    ) {
        this.backOrderRepository = backOrderRepository;
        this.backOrderAuditRepository = backOrderAuditRepository;
    }

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void auditBackOrders() {
        backOrderRepository.flush();
        LocalDateTime auditedAt = LocalDateTime.now();
        backOrderAuditRepository.save(new BackOrderAudit(backOrderRepository.count(), auditedAt));
    }
}
