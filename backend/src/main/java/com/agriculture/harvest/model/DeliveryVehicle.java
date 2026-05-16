package com.agriculture.harvest.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryVehicle {
    private String id;
    private String plateNumber;
    private Double maxCapacity;
    private Double currentCapacity;
    private List<String> orderIds = new ArrayList<>();
    private String status;
    private String currentRegion;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public Double getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Double maxCapacity) { this.maxCapacity = maxCapacity; }
    public Double getCurrentCapacity() { return currentCapacity; }
    public void setCurrentCapacity(Double currentCapacity) { this.currentCapacity = currentCapacity; }
    public List<String> getOrderIds() { return orderIds; }
    public void setOrderIds(List<String> orderIds) { this.orderIds = orderIds; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentRegion() { return currentRegion; }
    public void setCurrentRegion(String currentRegion) { this.currentRegion = currentRegion; }
}