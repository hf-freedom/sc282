package com.agriculture.harvest.controller;

import com.agriculture.harvest.dto.CommonResult;
import com.agriculture.harvest.model.HarvestTask;
import com.agriculture.harvest.service.HarvestTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class HarvestTaskController {

    @Autowired
    private HarvestTaskService taskService;

    @PostMapping("/generate")
    public CommonResult<List<HarvestTask>> generateTasks() {
        return CommonResult.success(taskService.generateTasks());
    }

    @GetMapping
    public CommonResult<List<HarvestTask>> getAllTasks() {
        return CommonResult.success(taskService.getAllTasks());
    }

    @PutMapping("/{id}/assign")
    public CommonResult<HarvestTask> assignTask(@PathVariable String id, @RequestParam String assignee) {
        return CommonResult.success(taskService.assignTask(id, assignee));
    }

    @PutMapping("/{id}/complete")
    public CommonResult<HarvestTask> completeTask(@PathVariable String id, @RequestParam Double actualQuantity) {
        return CommonResult.success(taskService.completeTask(id, actualQuantity));
    }
}