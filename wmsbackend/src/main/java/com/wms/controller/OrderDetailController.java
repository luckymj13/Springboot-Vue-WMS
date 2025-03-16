package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.common.Result;
import com.wms.entity.OrderDetail;
import com.wms.service.IOrderDetailService;
import com.wms.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private IOrderDetailService orderDetailService;

    @PostMapping("/add")
    public Result add(@RequestBody OrderDetail detail) {
        detail.setIsValid(1);
        return orderDetailService.save(detail) ? Result.success() : Result.fail();
    }

    @PostMapping("/addBatch")
    public Result addBatch(@RequestBody List<OrderDetail> details) {
        return orderDetailService.saveBatch(details) ? Result.success() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody OrderDetail detail) {
        return orderDetailService.updateDetail(detail) ? Result.success() : Result.fail();
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        OrderDetail detail = orderDetailService.getById(id);
        if(detail != null) {
            detail.setIsValid(0);
            return orderDetailService.updateById(detail) ? Result.success() : Result.fail();
        }
        return Result.fail();
    }

    @GetMapping("/deleteByOrderId")
    public Result deleteByOrderId(@RequestParam Integer orderId) {
        return orderDetailService.deleteByOrderId(orderId) ? Result.success() : Result.fail();
    }

    @GetMapping("/getByOrderId")
    public Result getByOrderId(@RequestParam Integer orderId) {
        List<OrderDetail> details = orderDetailService.getByOrderId(orderId);
        return Result.success(details);
    }

    @GetMapping("/getDetailVO")
    public Result getDetailVO(@RequestParam Integer orderId) {
        List<OrderDetailVO> details = orderDetailService.getDetailVOByOrderId(orderId);
        return Result.success(details);
    }
} 