package com.agriculture.harvest.service;

import com.agriculture.harvest.dto.OrderCreateDTO;
import com.agriculture.harvest.dto.OrderItemDTO;
import com.agriculture.harvest.model.HarvestBatch;
import com.agriculture.harvest.model.Order;
import com.agriculture.harvest.model.OrderItem;
import com.agriculture.harvest.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private BatchService batchService;

    @Autowired
    private HarvestTaskService harvestTaskService;

    public Order createOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(dto.getUserId());
        order.setUserName(dto.getUserName());
        order.setAddress(dto.getAddress());
        order.setRegion(dto.getRegion());
        order.setPriority(dto.getPriority() != null ? dto.getPriority() : 1);
        order.setStatus("PENDING");
        order.setTotalQuantity(0.0);
        order.setShippedQuantity(0.0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        double totalQty = 0.0;
        boolean canLock = true;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            HarvestBatch batch = batchService.getBatch(itemDTO.getBatchId());
            if (batch == null) {
                throw new RuntimeException("批次不存在: " + itemDTO.getBatchId());
            }
            if (!batchService.lockQuantity(itemDTO.getBatchId(), itemDTO.getQuantity())) {
                canLock = false;
                break;
            }

            OrderItem item = new OrderItem();
            item.setId(UUID.randomUUID().toString());
            item.setOrderId(order.getId());
            item.setBatchId(itemDTO.getBatchId());
            item.setProductName(batch.getProductName());
            item.setQuantity(itemDTO.getQuantity());
            item.setLockedQuantity(itemDTO.getQuantity());
            item.setShippedQuantity(0.0);
            storage.saveOrderItem(item);
            order.getItems().add(item);
            totalQty += itemDTO.getQuantity();
        }

        if (!canLock) {
            for (OrderItem item : order.getItems()) {
                batchService.unlockQuantity(item.getBatchId(), item.getLockedQuantity());
                storage.orderItemMap.remove(item.getId());
            }
            throw new RuntimeException("库存不足，无法锁定");
        }

        order.setTotalQuantity(totalQty);
        order.setStatus("LOCKED");
        storage.saveOrder(order);
        return order;
    }

    public List<Order> getAllOrders() {
        return storage.getAllOrders();
    }

    public Order getOrder(String id) {
        return storage.getOrder(id);
    }

    public Order cancelOrder(String orderId) {
        Order order = storage.getOrder(orderId);
        if (order != null && !"CANCELLED".equals(order.getStatus())) {
            for (OrderItem item : order.getItems()) {
                batchService.unlockQuantity(item.getBatchId(), item.getLockedQuantity());
            }
            order.setStatus("CANCELLED");
            order.setRemark("订单已取消，产量已释放");
            order.setUpdateTime(java.time.LocalDateTime.now());
            storage.saveOrder(order);
            
            harvestTaskService.recalculateTasksForOrder(orderId);
        }
        return order;
    }

    public Order updateOrderStatus(String orderId, String status) {
        Order order = storage.getOrder(orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdateTime(java.time.LocalDateTime.now());
            storage.saveOrder(order);
        }
        return order;
    }

    public Order confirmPartialShip(String orderId, String remark) {
        Order order = storage.getOrder(orderId);
        if (order != null) {
            order.setStatus("PARTIAL_SHIPPED");
            if (remark != null && !remark.isEmpty()) {
                order.setRemark(remark);
            } else {
                order.setRemark("已确认部分发货");
            }
            order.setUpdateTime(java.time.LocalDateTime.now());
            storage.saveOrder(order);
        }
        return order;
    }

    public Order confirmDelay(String orderId, String remark) {
        Order order = storage.getOrder(orderId);
        if (order != null) {
            order.setStatus("DELAYED");
            if (remark != null && !remark.isEmpty()) {
                order.setRemark(remark);
            } else {
                order.setRemark("已确认延期");
            }
            order.setUpdateTime(java.time.LocalDateTime.now());
            storage.saveOrder(order);
        }
        return order;
    }
}