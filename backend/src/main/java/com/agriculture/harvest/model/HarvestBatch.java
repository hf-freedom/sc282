package com.agriculture.harvest.model;

import java.time.LocalDateTime;

public class HarvestBatch {
    private String id;
    private String farmerId;
    private String farmerName;
    private String productName;
    private String region;
    private Double totalQuantity;
    private Double availableQuantity;
    private Double lockedQuantity;
    private Double harvestedQuantity;
    private Integer maturityLevel;
    private LocalDateTime matureTime;
    private LocalDateTime expireTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }
    public Double getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Double availableQuantity) { this.availableQuantity = availableQuantity; }
    public Double getLockedQuantity() { return lockedQuantity; }
    public void setLockedQuantity(Double lockedQuantity) { this.lockedQuantity = lockedQuantity; }
    public Double getHarvestedQuantity() { return harvestedQuantity; }
    public void setHarvestedQuantity(Double harvestedQuantity) { this.harvestedQuantity = harvestedQuantity; }
    public Integer getMaturityLevel() { return maturityLevel; }
    public void setMaturityLevel(Integer maturityLevel) { this.maturityLevel = maturityLevel; }
    public LocalDateTime getMatureTime() { return matureTime; }
    public void setMatureTime(LocalDateTime matureTime) { this.matureTime = matureTime; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}