package com.agriculture.harvest.dto;

import java.time.LocalDateTime;

public class BatchCreateDTO {
    private String farmerId;
    private String farmerName;
    private String productName;
    private String region;
    private Double totalQuantity;
    private LocalDateTime matureTime;
    private Integer expireHours;

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
    public LocalDateTime getMatureTime() { return matureTime; }
    public void setMatureTime(LocalDateTime matureTime) { this.matureTime = matureTime; }
    public Integer getExpireHours() { return expireHours; }
    public void setExpireHours(Integer expireHours) { this.expireHours = expireHours; }
}