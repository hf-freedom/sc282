package com.agriculture.harvest.model;

public class OrderItem {
    private String id;
    private String orderId;
    private String batchId;
    private String productName;
    private Double quantity;
    private Double lockedQuantity;
    private Double shippedQuantity;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public Double getLockedQuantity() { return lockedQuantity; }
    public void setLockedQuantity(Double lockedQuantity) { this.lockedQuantity = lockedQuantity; }
    public Double getShippedQuantity() { return shippedQuantity; }
    public void setShippedQuantity(Double shippedQuantity) { this.shippedQuantity = shippedQuantity; }
}