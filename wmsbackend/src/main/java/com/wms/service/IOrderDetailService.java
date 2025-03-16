package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.OrderDetail;
import com.wms.vo.OrderDetailVO;

import java.util.List;

public interface IOrderDetailService extends IService<OrderDetail> {
    // 根据订单ID获取明细
    List<OrderDetail> getByOrderId(Integer orderId);
    
    // 批量保存订单明细
    boolean saveBatch(List<OrderDetail> details);
    
    // 更新订单明细
    boolean updateDetail(OrderDetail detail);
    
    // 删除订单明细
    boolean deleteByOrderId(Integer orderId);
    
    // 获取订单明细VO列表
    List<OrderDetailVO> getDetailVOByOrderId(Integer orderId);
} 