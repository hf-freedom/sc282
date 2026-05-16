package com.agriculture.harvest.storage;

import com.agriculture.harvest.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {

    public final Map<String, HarvestBatch> batchMap = new ConcurrentHashMap<>();
    public final Map<String, Order> orderMap = new ConcurrentHashMap<>();
    public final Map<String, OrderItem> orderItemMap = new ConcurrentHashMap<>();
    public final Map<String, HarvestTask> taskMap = new ConcurrentHashMap<>();
    public final Map<String, DeliveryVehicle> vehicleMap = new ConcurrentHashMap<>();
    public final Map<String, Delivery> deliveryMap = new ConcurrentHashMap<>();

    public List<HarvestBatch> getAllBatches() {
        return new ArrayList<>(batchMap.values());
    }

    public void saveBatch(HarvestBatch batch) {
        batchMap.put(batch.getId(), batch);
    }

    public HarvestBatch getBatch(String id) {
        return batchMap.get(id);
    }

    public void removeBatch(String id) {
        batchMap.remove(id);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderMap.values());
    }

    public void saveOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public Order getOrder(String id) {
        return orderMap.get(id);
    }

    public void removeOrder(String id) {
        orderMap.remove(id);
    }

    public List<OrderItem> getAllOrderItems() {
        return new ArrayList<>(orderItemMap.values());
    }

    public void saveOrderItem(OrderItem item) {
        orderItemMap.put(item.getId(), item);
    }

    public OrderItem getOrderItem(String id) {
        return orderItemMap.get(id);
    }

    public List<HarvestTask> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public void saveTask(HarvestTask task) {
        taskMap.put(task.getId(), task);
    }

    public HarvestTask getTask(String id) {
        return taskMap.get(id);
    }

    public void removeTask(String id) {
        taskMap.remove(id);
    }

    public List<DeliveryVehicle> getAllVehicles() {
        return new ArrayList<>(vehicleMap.values());
    }

    public void saveVehicle(DeliveryVehicle vehicle) {
        vehicleMap.put(vehicle.getId(), vehicle);
    }

    public DeliveryVehicle getVehicle(String id) {
        return vehicleMap.get(id);
    }

    public List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveryMap.values());
    }

    public void saveDelivery(Delivery delivery) {
        deliveryMap.put(delivery.getId(), delivery);
    }

    public Delivery getDelivery(String id) {
        return deliveryMap.get(id);
    }
}