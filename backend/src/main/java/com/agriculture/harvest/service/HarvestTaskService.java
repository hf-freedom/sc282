package com.agriculture.harvest.service;

import com.agriculture.harvest.enums.OrderStatus;
import com.agriculture.harvest.model.HarvestBatch;
import com.agriculture.harvest.model.HarvestTask;
import com.agriculture.harvest.model.Order;
import com.agriculture.harvest.model.OrderItem;
import com.agriculture.harvest.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HarvestTaskService {

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private BatchService batchService;

    public List<HarvestTask> generateTasks() {
        List<Order> lockedOrders = storage.getAllOrders().stream()
                .filter(o -> "LOCKED".equals(o.getStatus()))
                .sorted(Comparator.comparing(Order::getPriority).reversed()
                        .thenComparing(Order::getCreateTime))
                .collect(Collectors.toList());

        Map<String, List<Order>> regionOrdersMap = lockedOrders.stream()
                .collect(Collectors.groupingBy(Order::getRegion));

        List<HarvestTask> tasks = new ArrayList<>();

        for (Map.Entry<String, List<Order>> entry : regionOrdersMap.entrySet()) {
            String region = entry.getKey();
            List<Order> orders = entry.getValue();

            Map<String, List<Order>> batchOrdersMap = new HashMap<>();
            for (Order order : orders) {
                for (OrderItem item : order.getItems()) {
                    batchOrdersMap.computeIfAbsent(item.getBatchId(), k -> new ArrayList<>())
                            .add(order);
                }
            }

            for (Map.Entry<String, List<Order>> batchEntry : batchOrdersMap.entrySet()) {
                String batchId = batchEntry.getKey();
                HarvestBatch batch = batchService.getBatch(batchId);
                if (batch != null && batch.getMaturityLevel() >= 10) {
                    List<Order> batchOrders = batchEntry.getValue();
                    double totalQty = batchOrders.stream()
                            .flatMap(o -> o.getItems().stream())
                            .filter(i -> i.getBatchId().equals(batchId))
                            .mapToDouble(OrderItem::getLockedQuantity)
                            .sum();

                    HarvestTask task = new HarvestTask();
                    task.setId(UUID.randomUUID().toString());
                    task.setBatchId(batchId);
                    task.setBatchRegion(region);
                    task.setProductName(batch.getProductName());
                    task.setMaturityLevel(batch.getMaturityLevel());
                    task.setTargetQuantity(Math.min(totalQty, batch.getLockedQuantity()));
                    task.setActualQuantity(0.0);
                    task.setStatus("PENDING");
                    task.setCreateTime(LocalDateTime.now());

                    batchOrders.forEach(o -> task.getOrderIds().add(o.getId()));

                    storage.saveTask(task);
                    tasks.add(task);
                }
            }
        }

        return tasks;
    }

    public List<HarvestTask> getAllTasks() {
        return storage.getAllTasks();
    }

    public void recalculateTasksForOrder(String orderId) {
        List<HarvestTask> affectedTasks = storage.getAllTasks().stream()
                .filter(task -> task.getOrderIds().contains(orderId))
                .filter(task -> "PENDING".equals(task.getStatus()) || "ASSIGNED".equals(task.getStatus()))
                .collect(Collectors.toList());

        for (HarvestTask task : affectedTasks) {
            task.getOrderIds().remove(orderId);

            if (task.getOrderIds().isEmpty()) {
                storage.removeTask(task.getId());
            } else {
                double totalQty = 0;
                for (String oId : task.getOrderIds()) {
                    Order order = storage.getOrder(oId);
                    if (order != null) {
                        for (OrderItem item : order.getItems()) {
                            if (item.getBatchId().equals(task.getBatchId())) {
                                totalQty += item.getLockedQuantity();
                            }
                        }
                    }
                }
                task.setTargetQuantity(totalQty);
                storage.saveTask(task);
            }
        }
    }

    public HarvestTask assignTask(String taskId, String assignee) {
        HarvestTask task = storage.getTask(taskId);
        if (task != null) {
            task.setAssignee(assignee);
            task.setStatus("ASSIGNED");
            task.setAssignTime(LocalDateTime.now());
            storage.saveTask(task);
        }
        return task;
    }

    public HarvestTask completeTask(String taskId, Double actualQuantity) {
        HarvestTask task = storage.getTask(taskId);
        if (task != null) {
            task.setActualQuantity(actualQuantity);
            task.setStatus("COMPLETED");
            task.setCompleteTime(LocalDateTime.now());

            batchService.harvestQuantity(task.getBatchId(), actualQuantity);

            double diff = task.getTargetQuantity() - actualQuantity;
            if (diff > 0) {
                handleHarvestShortage(task, diff);
            }

            storage.saveTask(task);
        }
        return task;
    }

    private void handleHarvestShortage(HarvestTask task, double shortage) {
        List<Order> orders = task.getOrderIds().stream()
                .map(storage::getOrder)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Order::getPriority))
                .collect(Collectors.toList());

        double remainingShortage = shortage;

        for (Order order : orders) {
            if (remainingShortage <= 0) break;

            for (OrderItem item : order.getItems()) {
                if (item.getBatchId().equals(task.getBatchId())) {
                    double canReduce = Math.min(item.getLockedQuantity(), remainingShortage);
                    item.setLockedQuantity(item.getLockedQuantity() - canReduce);
                    batchService.unlockQuantity(task.getBatchId(), canReduce);
                    remainingShortage -= canReduce;

                    if (item.getLockedQuantity() < item.getQuantity()) {
                        order.setStatus("PARTIAL_SHIPPED");
                    }
                    storage.saveOrderItem(item);
                }
            }

            if ("PARTIAL_SHIPPED".equals(order.getStatus())) {
                order.setRemark("采摘不足，部分发货");
            }
            storage.saveOrder(order);
        }

        if (remainingShortage > 0) {
            for (Order order : orders) {
                if ("LOCKED".equals(order.getStatus())) {
                    order.setStatus("DELAYED");
                    order.setRemark("采摘不足，订单延期");
                    storage.saveOrder(order);
                }
            }
        }
    }
}