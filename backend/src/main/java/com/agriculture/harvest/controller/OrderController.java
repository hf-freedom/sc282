package com.agriculture.harvest.controller;

import com.agriculture.harvest.dto.CommonResult;
import com.agriculture.harvest.dto.OrderCreateDTO;
import com.agriculture.harvest.model.Order;
import com.agriculture.harvest.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public CommonResult<Order> createOrder(@RequestBody OrderCreateDTO dto) {
        try {
            return CommonResult.success(orderService.createOrder(dto));
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @GetMapping
    public CommonResult<List<Order>> getAllOrders() {
        return CommonResult.success(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public CommonResult<Order> getOrder(@PathVariable String id) {
        return CommonResult.success(orderService.getOrder(id));
    }

    @PutMapping("/{id}/cancel")
    public CommonResult<Order> cancelOrder(@PathVariable String id) {
        return CommonResult.success(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/status")
    public CommonResult<Order> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        return CommonResult.success(orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/partial-ship")
    public CommonResult<Order> confirmPartialShip(@PathVariable String id, @RequestParam(required = false) String remark) {
        return CommonResult.success(orderService.confirmPartialShip(id, remark));
    }

    @PutMapping("/{id}/delay")
    public CommonResult<Order> confirmDelay(@PathVariable String id, @RequestParam(required = false) String remark) {
        return CommonResult.success(orderService.confirmDelay(id, remark));
    }
}