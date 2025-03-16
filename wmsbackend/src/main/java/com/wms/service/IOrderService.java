package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Order;

public interface IOrderService extends IService<Order> {
    // 创建订单
    boolean createOrder(Order order);
    
    // 审核订单
    boolean reviewOrder(Integer orderId, Integer reviewerId, String status);
    
    // 确认订单
    boolean confirmOrder(Integer orderId, Integer confirmerId);
    
    // 完成订单
    boolean completeOrder(Integer orderId);

    // 处理采购入库
    boolean processPurchaseIn(Integer orderId, Integer storageId);
    
    // 处理销售出库
    boolean processSaleOut(Integer orderId, Integer storageId);
    
    // 获取带有供应商名称的采购订单列表
    IPage<Order> getPurchaseOrdersWithSupplier(Page<Order> page, QueryWrapper<Order> queryWrapper);
    
    // 获取带有客户名称的销售订单列表
    IPage<Order> getSalesOrdersWithCustomer(Page<Order> page, QueryWrapper<Order> queryWrapper);
    
    // 获取带有操作人姓名的订单详情
    Order getOrderWithOperatorNames(Integer orderId);
    
    // 填充订单列表中的操作人姓名
    void fillOrdersWithOperatorNames(IPage<Order> orderPage);
    
    // 取消订单
    boolean cancelOrder(Integer orderId);
} 