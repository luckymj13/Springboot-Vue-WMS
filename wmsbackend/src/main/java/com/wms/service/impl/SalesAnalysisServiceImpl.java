package com.wms.service.impl;

import com.wms.mapper.OrderDetailMapper;
import com.wms.mapper.OrderMapper;
import com.wms.service.ISalesAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesAnalysisServiceImpl implements ISalesAnalysisService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public Map<String, Object> getSalesTrend(String startDate, String endDate) {
        List<Map<String, Object>> dailySales = orderMapper.getDailySales(startDate, endDate);
        List<Map<String, Object>> monthlySales = orderMapper.getMonthlySales(startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("daily", dailySales);
        result.put("monthly", monthlySales);
        return result;
    }

    @Override
    public List<Map<String, Object>> getCustomerRanking(String startDate, String endDate) {
        return orderMapper.getCustomerRanking(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getGoodsRanking(String startDate, String endDate) {
        return orderDetailMapper.getGoodsRanking(startDate, endDate);
    }

    @Override
    public Map<String, Object> getSalesSummary(String startDate, String endDate) {
        return orderMapper.getSalesSummary(startDate, endDate);
    }
} 