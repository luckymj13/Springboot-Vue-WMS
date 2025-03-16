package com.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.entity.OrderDetail;
import com.wms.vo.OrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    
    @Select("SELECT od.*, g.name as goods_name " +
            "FROM order_details od " +
            "LEFT JOIN goods g ON od.goods_id = g.id " +
            "WHERE od.order_id = #{orderId} AND od.is_valid = 1")
    List<OrderDetailVO> getDetailVOByOrderId(Integer orderId);

    @Select("SELECT g.name as goods_name, " +
            "SUM(od.quantity) as total_quantity, " +
            "SUM(od.total_price) as total_amount " +
            "FROM order_details od " +
            "JOIN orders o ON od.order_id = o.order_id " +
            "JOIN goods g ON od.goods_id = g.id " +
            "WHERE o.order_type = '2' " +
            "AND o.status = '4' " +
            "AND o.created_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY od.goods_id " +
            "ORDER BY total_amount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getGoodsRanking(String startDate, String endDate);
} 