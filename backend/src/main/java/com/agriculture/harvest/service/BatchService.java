package com.agriculture.harvest.service;

import com.agriculture.harvest.dto.BatchCreateDTO;
import com.agriculture.harvest.model.HarvestBatch;
import com.agriculture.harvest.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BatchService {

    @Autowired
    private InMemoryStorage storage;

    public HarvestBatch createBatch(BatchCreateDTO dto) {
        HarvestBatch batch = new HarvestBatch();
        batch.setId(UUID.randomUUID().toString());
        batch.setFarmerId(dto.getFarmerId());
        batch.setFarmerName(dto.getFarmerName());
        batch.setProductName(dto.getProductName());
        batch.setRegion(dto.getRegion());
        batch.setTotalQuantity(dto.getTotalQuantity());
        batch.setAvailableQuantity(dto.getTotalQuantity());
        batch.setLockedQuantity(0.0);
        batch.setHarvestedQuantity(0.0);
        batch.setMaturityLevel(0);
        batch.setMatureTime(dto.getMatureTime());
        batch.setExpireTime(dto.getMatureTime().plusHours(dto.getExpireHours() != null ? dto.getExpireHours() : 72));
        batch.setStatus("PENDING");
        batch.setCreateTime(LocalDateTime.now());
        batch.setUpdateTime(LocalDateTime.now());
        storage.saveBatch(batch);
        return batch;
    }

    public List<HarvestBatch> getAllBatches() {
        return storage.getAllBatches();
    }

    public HarvestBatch getBatch(String id) {
        return storage.getBatch(id);
    }

    public HarvestBatch updateMaturity(String batchId, Integer maturityLevel) {
        HarvestBatch batch = storage.getBatch(batchId);
        if (batch != null) {
            batch.setMaturityLevel(maturityLevel);
            batch.setUpdateTime(LocalDateTime.now());
            if (maturityLevel >= 100) {
                batch.setStatus("MATURE");
            }
            storage.saveBatch(batch);
        }
        return batch;
    }

    public boolean lockQuantity(String batchId, Double quantity) {
        HarvestBatch batch = storage.getBatch(batchId);
        if (batch != null && batch.getAvailableQuantity() >= quantity) {
            batch.setAvailableQuantity(batch.getAvailableQuantity() - quantity);
            batch.setLockedQuantity(batch.getLockedQuantity() + quantity);
            batch.setUpdateTime(LocalDateTime.now());
            storage.saveBatch(batch);
            return true;
        }
        return false;
    }

    public void unlockQuantity(String batchId, Double quantity) {
        HarvestBatch batch = storage.getBatch(batchId);
        if (batch != null) {
            batch.setAvailableQuantity(batch.getAvailableQuantity() + quantity);
            batch.setLockedQuantity(batch.getLockedQuantity() - quantity);
            batch.setUpdateTime(LocalDateTime.now());
            storage.saveBatch(batch);
        }
    }

    public void harvestQuantity(String batchId, Double quantity) {
        HarvestBatch batch = storage.getBatch(batchId);
        if (batch != null) {
            batch.setLockedQuantity(batch.getLockedQuantity() - quantity);
            batch.setHarvestedQuantity(batch.getHarvestedQuantity() + quantity);
            batch.setUpdateTime(LocalDateTime.now());
            storage.saveBatch(batch);
        }
    }
}