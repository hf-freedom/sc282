package com.agriculture.harvest.controller;

import com.agriculture.harvest.dto.BatchCreateDTO;
import com.agriculture.harvest.dto.CommonResult;
import com.agriculture.harvest.model.HarvestBatch;
import com.agriculture.harvest.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @PostMapping
    public CommonResult<HarvestBatch> createBatch(@RequestBody BatchCreateDTO dto) {
        return CommonResult.success(batchService.createBatch(dto));
    }

    @GetMapping
    public CommonResult<List<HarvestBatch>> getAllBatches() {
        return CommonResult.success(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    public CommonResult<HarvestBatch> getBatch(@PathVariable String id) {
        return CommonResult.success(batchService.getBatch(id));
    }

    @PutMapping("/{id}/maturity")
    public CommonResult<HarvestBatch> updateMaturity(@PathVariable String id, @RequestParam Integer level) {
        return CommonResult.success(batchService.updateMaturity(id, level));
    }
}