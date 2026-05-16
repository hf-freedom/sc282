package com.agriculture.harvest.service;

import com.agriculture.harvest.model.HarvestBatch;
import com.agriculture.harvest.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledTaskService {

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private BatchService batchService;

    @Scheduled(fixedRate = 60000)
    public void updateMaturity() {
        LocalDateTime now = LocalDateTime.now();
        for (HarvestBatch batch : storage.getAllBatches()) {
            if ("PENDING".equals(batch.getStatus()) || "MATURE".equals(batch.getStatus())) {
                if (now.isAfter(batch.getMatureTime()) && batch.getMaturityLevel() < 100) {
                    int newLevel = Math.min(batch.getMaturityLevel() + 5, 100);
                    batchService.updateMaturity(batch.getId(), newLevel);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkExpiredBatches() {
        LocalDateTime now = LocalDateTime.now();
        for (HarvestBatch batch : storage.getAllBatches()) {
            if (!"EXPIRED".equals(batch.getStatus()) && now.isAfter(batch.getExpireTime())) {
                batch.setStatus("EXPIRED");
                batch.setUpdateTime(LocalDateTime.now());
                if (batch.getAvailableQuantity() > 0) {
                    batch.setAvailableQuantity(batch.getAvailableQuantity() * 0.5);
                }
                storage.saveBatch(batch);
            }
        }
    }

    @Scheduled(fixedRate = 300000)
    public void regenerateHarvestTasks() {
    }
}