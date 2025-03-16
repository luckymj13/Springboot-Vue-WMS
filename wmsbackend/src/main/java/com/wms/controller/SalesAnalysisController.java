package com.wms.controller;

import com.wms.common.Result;
import com.wms.service.ISalesAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salesAnalysis")
public class SalesAnalysisController {

    @Autowired
    private ISalesAnalysisService salesAnalysisService;

    @GetMapping("/trend")
    public Result getSalesTrend(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(salesAnalysisService.getSalesTrend(startDate, endDate));
    }

    @GetMapping("/customerRanking")
    public Result getCustomerRanking(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(salesAnalysisService.getCustomerRanking(startDate, endDate));
    }

    @GetMapping("/goodsRanking")
    public Result getGoodsRanking(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(salesAnalysisService.getGoodsRanking(startDate, endDate));
    }

    @GetMapping("/summary")
    public Result getSalesSummary(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(salesAnalysisService.getSalesSummary(startDate, endDate));
    }
} 