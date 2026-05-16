package com.agriculture.harvest.service;

import com.agriculture.harvest.model.Delivery;
import com.agriculture.harvest.model.DeliveryVehicle;
import com.agriculture.harvest.model.Order;
import com.agriculture.harvest.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private InMemoryStorage storage;

    @PostConstruct
    public void initVehicles() {
        String[] plates = {"京A12345", "京B67890", "京C11111"};
        double[] capacities = {1000.0, 800.0, 500.0};
        String[] regions = {"东区", "西区", "南区"};

        for (int i = 0; i < plates.length; i++) {
            DeliveryVehicle vehicle = new DeliveryVehicle();
            vehicle.setId(UUID.randomUUID().toString());
            vehicle.setPlateNumber(plates[i]);
            vehicle.setMaxCapacity(capacities[i]);
            vehicle.setCurrentCapacity(0.0);
            vehicle.setStatus("IDLE");
            vehicle.setCurrentRegion(regions[i]);
            storage.saveVehicle(vehicle);
        }
    }

    public List<DeliveryVehicle> getAllVehicles() {
        return storage.getAllVehicles();
    }

    public List<Delivery> getAllDeliveries() {
        return storage.getAllDeliveries();
    }

    public Delivery createDelivery(String vehicleId) {
        DeliveryVehicle vehicle = storage.getVehicle(vehicleId);
        if (vehicle == null || !"IDLE".equals(vehicle.getStatus())) {
            throw new RuntimeException("车辆不可用");
        }

        List<Order> readyOrders = storage.getAllOrders().stream()
                .filter(o -> "PARTIAL_SHIPPED".equals(o.getStatus()) ||
                        "LOCKED".equals(o.getStatus()))
                .collect(Collectors.toList());

        if (readyOrders.isEmpty()) {
            throw new RuntimeException("没有可配送的订单，请先创建订单并锁定");
        }

        List<Order> regionOrders = readyOrders.stream()
                .filter(o -> o.getRegion().equals(vehicle.getCurrentRegion()))
                .collect(Collectors.toList());

        if (regionOrders.isEmpty()) {
            throw new RuntimeException("车辆区域[" + vehicle.getCurrentRegion() + "]没有待配送订单，" +
                    "现有订单区域: " + readyOrders.stream().map(Order::getRegion).distinct().collect(Collectors.toList()));
        }

        List<Order> highPriorityOrders = regionOrders.stream()
                .filter(o -> o.getPriority() >= 2)
                .sorted(Comparator.comparing(Order::getTotalQuantity))
                .collect(Collectors.toList());

        List<Order> lowPriorityOrders = regionOrders.stream()
                .filter(o -> o.getPriority() < 2)
                .sorted(Comparator.comparing(Order::getTotalQuantity))
                .collect(Collectors.toList());

        List<Order> sortedOrders = new ArrayList<>();
        sortedOrders.addAll(highPriorityOrders);
        sortedOrders.addAll(lowPriorityOrders);

        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID().toString());
        delivery.setVehicleId(vehicleId);
        delivery.setVehiclePlateNumber(vehicle.getPlateNumber());
        delivery.setStatus("DELIVERING");
        delivery.setCreateTime(LocalDateTime.now());
        delivery.setTotalCapacity(0.0);

        List<String> loadedOrders = new ArrayList<>();
        double totalCapacity = 0.0;
        List<String> skippedOrders = new ArrayList<>();

        for (Order order : sortedOrders) {
            if (totalCapacity + order.getTotalQuantity() <= vehicle.getMaxCapacity()) {
                loadedOrders.add(order.getId());
                totalCapacity += order.getTotalQuantity();
                order.setDeliveryId(delivery.getId());
                order.setStatus("SHIPPED");
                storage.saveOrder(order);
            } else {
                skippedOrders.add(order.getId());
            }
        }

        if (loadedOrders.isEmpty()) {
            throw new RuntimeException("所有订单数量超过车辆容量(" + vehicle.getMaxCapacity() + ")，无法装车。" +
                    "订单数量: " + regionOrders.stream().map(Order::getTotalQuantity).collect(Collectors.toList()));
        }

        delivery.setOrderIds(loadedOrders);
        delivery.setTotalCapacity(totalCapacity);
        String routeInfo = vehicle.getCurrentRegion() + "配送路线(共" + loadedOrders.size() + "单";
        if (!skippedOrders.isEmpty()) {
            routeInfo += "，" + skippedOrders.size() + "单因容量超限未装车";
        }
        routeInfo += ")";
        delivery.setRoute(routeInfo);

        vehicle.setStatus("DELIVERING");
        vehicle.setCurrentCapacity(totalCapacity);
        vehicle.getOrderIds().clear();
        vehicle.getOrderIds().addAll(loadedOrders);
        storage.saveVehicle(vehicle);

        storage.saveDelivery(delivery);
        return delivery;
    }

    public Delivery completeDelivery(String deliveryId) {
        Delivery delivery = storage.getDelivery(deliveryId);
        if (delivery != null) {
            delivery.setStatus("COMPLETED");
            storage.saveDelivery(delivery);

            DeliveryVehicle vehicle = storage.getVehicle(delivery.getVehicleId());
            if (vehicle != null) {
                vehicle.setStatus("IDLE");
                vehicle.setCurrentCapacity(0.0);
                vehicle.getOrderIds().clear();
                storage.saveVehicle(vehicle);
            }

            for (String orderId : delivery.getOrderIds()) {
                Order order = storage.getOrder(orderId);
                if (order != null) {
                    order.setStatus("COMPLETED");
                    storage.saveOrder(order);
                }
            }
        }
        return delivery;
    }
}