package com.agriculture.harvest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HarvestTask {
    private String id;
    private String batchId;
    private String batchRegion;
    private String productName;
    private Integer maturityLevel;
    private List<String> orderIds = new ArrayList<>();
    private Double targetQuantity;
    private Double actualQuantity;
    private String assignee;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime assignTime;
    private LocalDateTime completeTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getBatchRegion() { return batchRegion; }
    public void setBatchRegion(String batchRegion) { this.batchRegion = batchRegion; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getMaturityLevel() { return maturityLevel; }
    public void setMaturityLevel(Integer maturityLevel) { this.maturityLevel = maturityLevel; }
    public List<String> getOrderIds() { return orderIds; }
    public void setOrderIds(List<String> orderIds) { this.orderIds = orderIds; }
    public Double getTargetQuantity() { return targetQuantity; }
    public void setTargetQuantity(Double targetQuantity) { this.targetQuantity = targetQuantity; }
    public Double getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(Double actualQuantity) { this.actualQuantity = actualQuantity; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getAssignTime() { return assignTime; }
    public void setAssignTime(LocalDateTime assignTime) { this.assignTime = assignTime; }
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
}