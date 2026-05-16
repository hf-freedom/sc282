package com.agriculture.harvest.controller;

import com.agriculture.harvest.dto.CommonResult;
import com.agriculture.harvest.model.Delivery;
import com.agriculture.harvest.model.DeliveryVehicle;
import com.agriculture.harvest.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/vehicles")
    public CommonResult<List<DeliveryVehicle>> getAllVehicles() {
        return CommonResult.success(deliveryService.getAllVehicles());
    }

    @GetMapping
    public CommonResult<List<Delivery>> getAllDeliveries() {
        return CommonResult.success(deliveryService.getAllDeliveries());
    }

    @PostMapping("/create")
    public CommonResult<Delivery> createDelivery(@RequestParam String vehicleId) {
        try {
            return CommonResult.success(deliveryService.createDelivery(vehicleId));
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public CommonResult<Delivery> completeDelivery(@PathVariable String id) {
        return CommonResult.success(deliveryService.completeDelivery(id));
    }
}