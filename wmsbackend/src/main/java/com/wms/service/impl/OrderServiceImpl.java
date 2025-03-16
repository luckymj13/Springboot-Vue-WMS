package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Order;
import com.wms.entity.OrderDetail;
import com.wms.entity.Record;
import com.wms.entity.Goods;
import com.wms.entity.User;
import com.wms.mapper.OrderMapper;
import com.wms.mapper.UserMapper;
import com.wms.service.IOrderService;
import com.wms.service.IGoodsService;
import com.wms.service.IRecordService;
import com.wms.service.IOrderDetailService;
import com.wms.entity.Supplier;
import com.wms.entity.Customer;
import com.wms.mapper.SupplierMapper;
import com.wms.mapper.CustomerMapper;
import com.wms.mapper.RecordMapper;
import com.wms.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IRecordService recordService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean createOrder(Order order) {
        order.setCreatedTime(LocalDateTime.now());
        order.setStatus("1"); // 待审核
        order.setIsValid(1);
        
        // 根据订单类型设置关联方类型
        if ("1".equals(order.getOrderType())) {
            order.setRelatedPartyType("supplier"); // 采购订单关联供应商
        } else if ("2".equals(order.getOrderType())) {
            order.setRelatedPartyType("customer"); // 销售订单关联客户
        }
        
        return save(order);
    }

    @Override
    @Transactional
    public boolean reviewOrder(Integer orderId, Integer reviewerId, String status) {
        Order order = getById(orderId);
        if (order != null) {
            order.setReviewerId(reviewerId);
            order.setReviewTime(LocalDateTime.now());
            order.setStatus(status);
            return updateById(order);
        }
        return false;
    }

    @Override
    @Transactional 
    public boolean confirmOrder(Integer orderId, Integer confirmerId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setConfirmerId(confirmerId);
            order.setConfirmTime(LocalDateTime.now());
            order.setStatus("3"); // 已确认
            boolean updated = updateById(order);
            
            if (updated) {
                // 如果是采购订单，自动执行入库操作
                if ("1".equals(order.getOrderType())) {
                    try {
                        // 获取默认仓库ID（这里假设ID为1是默认仓库）
                        Integer defaultStorageId = 1;
                        processPurchaseIn(orderId, defaultStorageId);
                    } catch (Exception e) {
                        log.error("自动执行采购入库失败", e);
                        // 不影响订单确认状态，只记录日志
                    }
                }
                // 如果是销售订单，自动执行出库操作
                else if ("2".equals(order.getOrderType())) {
                    try {
                        // 获取默认仓库ID（这里假设ID为1是默认仓库）
                        Integer defaultStorageId = 1;
                        processSaleOut(orderId, defaultStorageId);
                    } catch (Exception e) {
                        log.error("自动执行销售出库失败", e);
                        // 不影响订单确认状态，只记录日志
                    }
                }
            }
            
            return updated;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean completeOrder(Integer orderId) {
        Order order = getById(orderId);
        if (order != null) {
            order.setCompleteTime(LocalDateTime.now());
            order.setStatus("4"); // 已完成
            return updateById(order);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean processPurchaseIn(Integer orderId, Integer storageId) {
        Order order = getById(orderId);
        log.info("Processing purchase in for order: {}", order);
        
        // 1. 基础检查
        if (order == null) {
            log.error("Order not found: {}", orderId);
            throw new RuntimeException("订单不存在");
        }
        
        if (!"1".equals(order.getOrderType().trim())) {
            log.error("Not a purchase order. Type: {}", order.getOrderType());
            throw new RuntimeException("不是采购订单");
        }
        
        if (!"3".equals(order.getStatus().trim())) {
            log.error("Order status not confirmed. Status: {}", order.getStatus());
            throw new RuntimeException("订单状态不是已确认");
        }

        // 2. 检查是否已经处理过
        QueryWrapper<Record> recordQuery = new QueryWrapper<>();
        recordQuery.eq("order_id", orderId)
                  .eq("operation_type", 1)  // 采购入库
                  .eq("inandout", 1);       // 入库
        
        if (recordService.count(recordQuery) > 0) {
            log.warn("Order {} has already been processed", orderId);
            throw new RuntimeException("该订单已经处理过");
        }
        
        // 3. 获取订单明细
        List<OrderDetail> details = orderDetailService.getByOrderId(orderId);
        log.info("Found order details: {}", details);
        
        if (details.isEmpty()) {
            log.error("No order details found for order: {}", orderId);
            throw new RuntimeException("订单明细不存在");
        }
        
        try {
            for (OrderDetail detail : details) {
                // 4. 更新库存
                Goods goods = goodsService.getById(detail.getGoodsId());
                if (goods == null) {
                    log.error("Goods not found: {}", detail.getGoodsId());
                    throw new RuntimeException("商品不存在");
                }
                
                log.info("Updating goods quantity. Current: {}, Adding: {}", 
                        goods.getCount(), detail.getQuantity());
                
                goods.setCount(goods.getCount() + detail.getQuantity());
                if (!goodsService.updateById(goods)) {
                    log.error("Failed to update goods quantity");
                    throw new RuntimeException("更新商品库存失败");
                }
                
                // 5. 创建入库记录
                Record record = new Record();
                record.setGoods(detail.getGoodsId());
                record.setUserid(order.getCreatorId());
                record.setCount(detail.getQuantity());
                record.setCreatetime(LocalDateTime.now());
                record.setOrderId(orderId);
                record.setOperationType(1); // 1采购入库
                record.setInandout(1); // 1入库
                record.setRemark("采购入库");
                
                if (!recordService.save(record)) {
                    log.error("Failed to save record");
                    throw new RuntimeException("保存入库记录失败");
                }
            }
            
            // 6. 更新订单状态
            order.setStatus("4"); // 已完成
            order.setCompleteTime(LocalDateTime.now());
            boolean result = updateById(order);
            log.info("Order status updated to completed: {}", result);
            return result;
            
        } catch (Exception e) {
            log.error("Error processing purchase in", e);
            throw new RuntimeException("入库处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean processSaleOut(Integer orderId, Integer storageId) {
        Order order = getById(orderId);
        log.info("Processing sale out for order: {}", order);
        
        // 1. 基础检查
        if (order == null) {
            log.error("Order not found: {}", orderId);
            throw new RuntimeException("订单不存在");
        }
        
        if (!"2".equals(order.getOrderType().trim())) {
            log.error("Not a sales order. Type: {}", order.getOrderType());
            throw new RuntimeException("不是销售订单");
        }
        
        // 检查订单状态
        if (!"3".equals(order.getStatus().trim())) {
            log.error("Order status not confirmed. Status: {}", order.getStatus());
            throw new RuntimeException("订单状态不是已确认");
        }
        
        // 2. 检查是否已经处理过
        QueryWrapper<Record> recordQuery = new QueryWrapper<>();
        recordQuery.eq("order_id", orderId)
                  .eq("operation_type", 2)  // 销售出库
                  .eq("inandout", 0);       // 出库
        
        if (recordService.count(recordQuery) > 0) {
            log.warn("Order {} has already been processed", orderId);
            throw new RuntimeException("该订单已经处理过");
        }
        
        // 3. 获取订单明细
        List<OrderDetail> details = orderDetailService.getByOrderId(orderId);
        log.info("Found order details: {}", details);
        
        if (details.isEmpty()) {
            log.error("No order details found for order: {}", orderId);
            throw new RuntimeException("订单明细不存在");
        }
        
        try {
            for (OrderDetail detail : details) {
                // 4. 检查并更新库存
                Goods goods = goodsService.getById(detail.getGoodsId());
                if (goods == null) {
                    log.error("Goods not found: {}", detail.getGoodsId());
                    throw new RuntimeException("商品不存在");
                }
                
                if (goods.getCount() < detail.getQuantity()) {
                    log.error("Insufficient stock. Required: {}, Available: {}", 
                            detail.getQuantity(), goods.getCount());
                    throw new RuntimeException("库存不足，商品：" + goods.getName() + "，需要：" + detail.getQuantity() + "，库存：" + goods.getCount());
                }
                
                log.info("Updating goods quantity. Current: {}, Subtracting: {}", 
                        goods.getCount(), detail.getQuantity());
                
                goods.setCount(goods.getCount() - detail.getQuantity());
                if (!goodsService.updateById(goods)) {
                    log.error("Failed to update goods quantity");
                    throw new RuntimeException("更新商品库存失败");
                }
                
                // 5. 创建出库记录
                Record record = new Record();
                record.setGoods(detail.getGoodsId());
                record.setUserid(order.getCreatorId());
                record.setCount(detail.getQuantity());
                record.setCreatetime(LocalDateTime.now());
                record.setOrderId(orderId);
                record.setOperationType(2); // 2销售出库
                record.setInandout(0); // 0出库
                record.setRemark("销售出库");
                
                if (!recordService.save(record)) {
                    log.error("Failed to save record");
                    throw new RuntimeException("保存出库记录失败");
                }
            }
            
            // 6. 更新订单状态
            order.setStatus("4"); // 已完成
            order.setCompleteTime(LocalDateTime.now());
            boolean result = updateById(order);
            log.info("Order status updated to completed: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error processing sale out", e);
            throw new RuntimeException("出库处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    public IPage<Order> getPurchaseOrdersWithSupplier(Page<Order> page, QueryWrapper<Order> queryWrapper) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        queryWrapper.eq("order_type", "1"); // 采购订单
        queryWrapper.eq("is_valid", 1);
        queryWrapper.eq("related_party_type", "supplier");
        queryWrapper.orderByDesc("created_time");
        
        IPage<Order> orderPage = this.page(page, queryWrapper);
        List<Order> orders = orderPage.getRecords();
        
        for (Order order : orders) {
            // 获取供应商名称
            Supplier supplier = supplierMapper.selectById(order.getRelatedPartyId());
            if (supplier != null) {
                order.setSupplierName(supplier.getSupplierName());
            }
        }
        
        // 填充操作人姓名
        fillOrdersWithOperatorNames(orderPage);
        
        return orderPage;
    }
    
    @Override
    public IPage<Order> getSalesOrdersWithCustomer(Page<Order> page, QueryWrapper<Order> queryWrapper) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        queryWrapper.eq("order_type", "2"); // 销售订单
        queryWrapper.eq("is_valid", 1);
        queryWrapper.eq("related_party_type", "customer");
        queryWrapper.orderByDesc("created_time");
        
        IPage<Order> orderPage = this.page(page, queryWrapper);
        List<Order> orders = orderPage.getRecords();
        
        for (Order order : orders) {
            // 获取客户名称
            Customer customer = customerMapper.selectById(order.getRelatedPartyId());
            if (customer != null) {
                order.setCustomerName(customer.getCustomerName());
            }
        }
        
        // 填充操作人姓名
        fillOrdersWithOperatorNames(orderPage);
        
        return orderPage;
    }
    
    @Override
    public Order getOrderWithOperatorNames(Integer orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            return null;
        }
        
        // 获取创建人姓名
        if (order.getCreatorId() != null) {
            User creator = userMapper.selectById(order.getCreatorId());
            if (creator != null) {
                order.setCreatorName(creator.getName());
            }
        }
        
        // 获取审核人姓名
        if (order.getReviewerId() != null) {
            User reviewer = userMapper.selectById(order.getReviewerId());
            if (reviewer != null) {
                order.setReviewerName(reviewer.getName());
            }
        }
        
        // 获取确认人姓名
        if (order.getConfirmerId() != null) {
            User confirmer = userMapper.selectById(order.getConfirmerId());
            if (confirmer != null) {
                order.setConfirmerName(confirmer.getName());
            }
        }
        
        // 获取关联方名称
        if ("supplier".equals(order.getRelatedPartyType())) {
            Supplier supplier = supplierMapper.selectById(order.getRelatedPartyId());
            if (supplier != null) {
                order.setSupplierName(supplier.getSupplierName());
            }
        } else if ("customer".equals(order.getRelatedPartyType())) {
            Customer customer = customerMapper.selectById(order.getRelatedPartyId());
            if (customer != null) {
                order.setCustomerName(customer.getCustomerName());
            }
        }
        
        // 获取订单详情
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderDetail> details = orderDetailService.list(queryWrapper);
        
        // 为订单详情添加商品名称
        if (details != null && !details.isEmpty()) {
            // 收集所有商品ID
            Set<Integer> goodsIds = details.stream()
                .map(OrderDetail::getGoodsId)
                .collect(Collectors.toSet());
            
            // 批量查询商品信息
            List<Goods> goodsList = goodsMapper.selectBatchIds(goodsIds);
            Map<Integer, String> goodsNameMap = goodsList.stream()
                .collect(Collectors.toMap(Goods::getId, Goods::getName));
            
            // 为每个订单详情设置商品名称
            for (OrderDetail detail : details) {
                detail.setGoodsName(goodsNameMap.get(detail.getGoodsId()));
            }
        }
        
        order.setDetails(details);
        
        return order;
    }
    
    @Override
    public void fillOrdersWithOperatorNames(IPage<Order> orderPage) {
        List<Order> orders = orderPage.getRecords();
        
        // 收集所有用户ID
        Set<Integer> userIds = new HashSet<>();
        for (Order order : orders) {
            if (order.getCreatorId() != null) userIds.add(order.getCreatorId());
            if (order.getReviewerId() != null) userIds.add(order.getReviewerId());
            if (order.getConfirmerId() != null) userIds.add(order.getConfirmerId());
        }
        
        // 批量查询用户信息
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            Map<Integer, String> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getName));
            
            // 填充操作人姓名
            for (Order order : orders) {
                if (order.getCreatorId() != null) {
                    order.setCreatorName(userMap.get(order.getCreatorId()));
                }
                if (order.getReviewerId() != null) {
                    order.setReviewerName(userMap.get(order.getReviewerId()));
                }
                if (order.getConfirmerId() != null) {
                    order.setConfirmerName(userMap.get(order.getConfirmerId()));
                }
            }
        }
    }

    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有待审核状态的订单可以取消
        if (!"1".equals(order.getStatus())) {
            throw new RuntimeException("只有待审核状态的订单可以取消");
        }
        
        // 更新订单状态为已取消(5)
        order.setStatus("5");
        return updateById(order);
    }
} 