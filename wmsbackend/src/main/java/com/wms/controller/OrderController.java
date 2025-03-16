package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Order;
import com.wms.entity.OrderDetail;
import com.wms.service.IOrderDetailService;
import com.wms.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private IOrderDetailService orderDetailService;

    // 创建订单
    @PostMapping("/create")
    public Result create(@RequestBody Order order) {
        try {
            // 生成订单编号
            String orderNumber = generateOrderNumber(order.getOrderType());
            order.setOrderNumber(orderNumber);
            
            // 提取订单详情
            List<OrderDetail> details = order.getDetails();
            
            // 创建订单
            boolean success = orderService.createOrder(order);
            if (!success) {
                return Result.fail("创建订单失败");
            }
            
            // 如果有订单详情，添加订单详情
            if (details != null && !details.isEmpty()) {
                // 为每个详情项设置订单ID
                for (OrderDetail detail : details) {
                    detail.setOrderId(order.getOrderId());
                    detail.setIsValid(1);
                }
                
                // 批量保存订单详情
                boolean detailSuccess = orderDetailService.saveBatch(details);
                if (!detailSuccess) {
                    return Result.fail("创建订单详情失败");
                }
            }
            
            return Result.success(order.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建订单失败: " + e.getMessage());
        }
    }

    // 审核订单
    @PostMapping("/review")
    public Result review(@RequestParam Integer orderId, 
                        @RequestParam Integer reviewerId,
                        @RequestParam String status) {
        return orderService.reviewOrder(orderId, reviewerId, status) ? 
               Result.success() : Result.fail();
    }

    // 确认订单
    @PostMapping("/confirm")
    public Result confirm(@RequestParam Integer orderId,
                         @RequestParam Integer confirmerId) {
        return orderService.confirmOrder(orderId, confirmerId) ? 
               Result.success() : Result.fail();
    }

    // 完成订单
    @PostMapping("/complete")
    public Result complete(@RequestParam Integer orderId) {
        return orderService.completeOrder(orderId) ? Result.success() : Result.fail();
    }

    // 分页查询
    @PostMapping("/page")
    public Result page(@RequestBody QueryPageParam query) {
        Page<Order> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        String orderType = (String) query.getParam().get("orderType");
        String orderNumber = (String) query.getParam().get("orderNumber");
        String status = (String) query.getParam().get("status");
        Object supplierId = query.getParam().get("supplierId");
        Object customerId = query.getParam().get("customerId");
        
        IPage<Order> result;
        
        if ("1".equals(orderType)) {
            // 采购订单，获取带供应商名称的订单列表
            result = orderService.getPurchaseOrdersWithSupplier(page, null);
            
            // 手动过滤结果
            if (result != null && result.getRecords() != null) {
                List<Order> filteredRecords = result.getRecords().stream()
                    .filter(order -> {
                        boolean match = true;
                        if (orderNumber != null && !orderNumber.isEmpty()) {
                            match = match && order.getOrderNumber().contains(orderNumber);
                        }
                        if (status != null && !status.isEmpty()) {
                            match = match && status.equals(order.getStatus());
                        }
                        if (supplierId != null) {
                            match = match && supplierId.equals(order.getRelatedPartyId());
                        }
                        return match;
                    })
                    .collect(Collectors.toList());
                
                // 更新结果
                long total = filteredRecords.size();
                int start = (int) ((page.getCurrent() - 1) * page.getSize());
                int end = Math.min(start + (int) page.getSize(), filteredRecords.size());
                
                if (start < filteredRecords.size()) {
                    filteredRecords = filteredRecords.subList(start, end);
                } else {
                    filteredRecords = new ArrayList<>();
                }
                
                Page<Order> filteredPage = new Page<>(page.getCurrent(), page.getSize(), total);
                filteredPage.setRecords(filteredRecords);
                result = filteredPage;
            }
        } else if ("2".equals(orderType)) {
            // 销售订单，获取带客户名称的订单列表
            result = orderService.getSalesOrdersWithCustomer(page, null);
            
            // 手动过滤结果
            if (result != null && result.getRecords() != null) {
                List<Order> filteredRecords = result.getRecords().stream()
                    .filter(order -> {
                        boolean match = true;
                        if (orderNumber != null && !orderNumber.isEmpty()) {
                            match = match && order.getOrderNumber().contains(orderNumber);
                        }
                        if (status != null && !status.isEmpty()) {
                            match = match && status.equals(order.getStatus());
                        }
                        if (customerId != null) {
                            match = match && customerId.equals(order.getRelatedPartyId());
                        }
                        return match;
                    })
                    .collect(Collectors.toList());
                
                // 更新结果
                long total = filteredRecords.size();
                int start = (int) ((page.getCurrent() - 1) * page.getSize());
                int end = Math.min(start + (int) page.getSize(), filteredRecords.size());
                
                if (start < filteredRecords.size()) {
                    filteredRecords = filteredRecords.subList(start, end);
                } else {
                    filteredRecords = new ArrayList<>();
                }
                
                Page<Order> filteredPage = new Page<>(page.getCurrent(), page.getSize(), total);
                filteredPage.setRecords(filteredRecords);
                result = filteredPage;
            }
        } else {
            // 如果没有指定订单类型，使用普通查询
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_valid", 1);
            
            if(orderNumber != null && !orderNumber.isEmpty()) {
                queryWrapper.like("order_number", orderNumber);
            }
            if(orderType != null && !orderType.isEmpty()) {
                queryWrapper.eq("order_type", orderType);
            }
            if(status != null && !status.isEmpty()) {
                queryWrapper.eq("status", status);
            }
            
            // 处理供应商ID查询
            if(supplierId != null) {
                queryWrapper.eq("related_party_id", supplierId);
                queryWrapper.eq("related_party_type", "supplier");
            }
            
            // 处理客户ID查询
            if(customerId != null) {
                queryWrapper.eq("related_party_id", customerId);
                queryWrapper.eq("related_party_type", "customer");
            }
            
            queryWrapper.orderByDesc("created_time");
            
            result = orderService.page(page, queryWrapper);
            
            // 填充操作人姓名
            orderService.fillOrdersWithOperatorNames(result);
        }
        
        return Result.success(result.getRecords(), result.getTotal());
    }

    // 生成订单编号
    private String generateOrderNumber(String type) {
        String prefix = "1".equals(type) ? "PO" : "SO"; // PO采购单 SO销售单
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%03d", (int)(Math.random() * 1000));
        return prefix + date + random;
    }

    @PostMapping("/purchaseIn")
    public Result purchaseIn(@RequestParam Integer orderId, @RequestParam Integer storageId) {
        try {
            return orderService.processPurchaseIn(orderId, storageId) ? Result.success() : Result.fail();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/saleOut")
    public Result saleOut(@RequestParam Integer orderId, @RequestParam Integer storageId) {
        try {
            return orderService.processSaleOut(orderId, storageId) ? Result.success() : Result.fail();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam Integer orderId) {
        try {
            return orderService.cancelOrder(orderId) ? Result.success() : Result.fail();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/createSampleDetails")
    public Result createSampleDetails(@RequestParam Integer orderId) {
        try {
            // 创建示例订单明细
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setGoodsId(1); // 假设ID为1的商品存在
            detail.setQuantity(1);
            detail.setUnitPrice(new java.math.BigDecimal("10.00"));
            detail.setTotalPrice(new java.math.BigDecimal("10.00"));
            detail.setRemark("示例订单明细");
            detail.setIsValid(1);
            
            // 保存订单明细
            return orderDetailService.save(detail) ? Result.success() : Result.fail();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result getOrderDetail(@PathVariable Integer id) {
        Order order = orderService.getOrderWithOperatorNames(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(order);
    }
} 