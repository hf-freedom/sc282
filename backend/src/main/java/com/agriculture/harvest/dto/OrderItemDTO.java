package com.agriculture.harvest.dto;

public class OrderItemDTO {
    private String batchId;
    private Double quantity;

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}