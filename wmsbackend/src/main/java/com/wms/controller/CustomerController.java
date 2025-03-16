package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Customer;
import com.wms.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @PostMapping("/add")
    public Result add(@RequestBody Customer customer) {
        if(customerService.checkCodeExists(customer.getCustomerCode())) {
            return Result.fail();
        }
        customer.setIsValid(1);
        return customerService.save(customer) ? Result.success() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Customer customer) {
        return customerService.updateById(customer) ? Result.success() : Result.fail();
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        Customer customer = customerService.getById(id);
        if(customer != null) {
            customer.setIsValid(0);
            return customerService.updateById(customer) ? Result.success() : Result.fail();
        }
        return Result.fail();
    }

    @PostMapping("/page")
    public Result page(@RequestBody QueryPageParam query) {
        Page<Customer> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_valid", 1);
        
        if(query.getParam().get("customerCode") != null) {
            queryWrapper.like("customer_code", query.getParam().get("customerCode"));
        }
        if(query.getParam().get("customerName") != null) {
            queryWrapper.like("customer_name", query.getParam().get("customerName")); 
        }
        if(query.getParam().get("customerLevel") != null) {
            queryWrapper.eq("customer_level", query.getParam().get("customerLevel"));
        }
        if(query.getParam().get("creditLevel") != null) {
            queryWrapper.eq("credit_level", query.getParam().get("creditLevel"));
        }

        IPage<Customer> result = customerService.page(page, queryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }

    @PostMapping("/updateLevel")
    public Result updateLevel(@RequestParam Integer customerId, @RequestParam Integer level) {
        return customerService.updateLevel(customerId, level) ? Result.success() : Result.fail();
    }

    @PostMapping("/updateCreditLevel")
    public Result updateCreditLevel(@RequestParam Integer customerId, @RequestParam Integer creditLevel) {
        return customerService.updateCreditLevel(customerId, creditLevel) ? Result.success() : Result.fail();
    }
} 