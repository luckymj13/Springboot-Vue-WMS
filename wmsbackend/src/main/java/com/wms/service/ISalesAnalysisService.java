package com.wms.service;

import java.util.List;
import java.util.Map;

public interface ISalesAnalysisService {
    // 获取销售趋势
    Map<String, Object> getSalesTrend(String startDate, String endDate);
    
    // 获取客户销售排行
    List<Map<String, Object>> getCustomerRanking(String startDate, String endDate);
    
    // 获取商品销售排行
    List<Map<String, Object>> getGoodsRanking(String startDate, String endDate);
    
    // 获取销售汇总数据
    Map<String, Object> getSalesSummary(String startDate, String endDate);
} 