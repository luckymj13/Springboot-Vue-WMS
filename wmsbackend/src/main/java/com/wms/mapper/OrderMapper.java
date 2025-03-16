package com.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wms.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    IPage<Order> getPurchaseOrdersWithSupplier(IPage<Order> page, @Param(Constants.WRAPPER) Wrapper<Order> queryWrapper);

    IPage<Order> getSalesOrdersWithCustomer(IPage<Order> page, @Param(Constants.WRAPPER) Wrapper<Order> queryWrapper);

    @Select("SELECT DATE(created_time) as date, " +
            "SUM(total_amount) as amount, " +
            "COUNT(*) as count " +
            "FROM orders " +
            "WHERE order_type = '2' " +
            "AND status = '4' " +
            "AND created_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_time) " +
            "ORDER BY date")
    List<Map<String, Object>> getDailySales(@Param("startDate") String startDate, 
                                          @Param("endDate") String endDate);

    @Select("SELECT DATE_FORMAT(created_time, '%Y-%m') as month, " +
            "SUM(total_amount) as amount, " +
            "COUNT(*) as count " +
            "FROM orders " +
            "WHERE order_type = '2' " +
            "AND status = '4' " +
            "AND created_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(created_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> getMonthlySales(@Param("startDate") String startDate, 
                                            @Param("endDate") String endDate);

    @Select("SELECT c.customer_name, " +
            "COUNT(o.order_id) as order_count, " +
            "SUM(o.total_amount) as total_amount " +
            "FROM orders o " +
            "JOIN customers c ON o.related_party_id = c.customer_id " +
            "WHERE o.order_type = '2' " +
            "AND o.status = '4' " +
            "AND o.created_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY c.customer_id " +
            "ORDER BY total_amount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getCustomerRanking(@Param("startDate") String startDate, 
                                               @Param("endDate") String endDate);

    @Select("SELECT COUNT(*) as total_orders, " +
            "SUM(total_amount) as total_amount, " +
            "AVG(total_amount) as avg_amount " +
            "FROM orders " +
            "WHERE order_type = '2' " +
            "AND status = '4' " +
            "AND created_time BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> getSalesSummary(@Param("startDate") String startDate, 
                                      @Param("endDate") String endDate);
} 