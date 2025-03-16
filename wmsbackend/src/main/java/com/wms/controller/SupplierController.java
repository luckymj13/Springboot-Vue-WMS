package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Supplier;
import com.wms.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private ISupplierService supplierService;

    @PostMapping("/add")
    public Result add(@RequestBody Supplier supplier) {
        if(supplierService.checkCodeExists(supplier.getSupplierCode())) {
            return Result.fail();
        }
        supplier.setIsValid(1);
        return supplierService.save(supplier) ? Result.success() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Supplier supplier) {
        return supplierService.updateById(supplier) ? Result.success() : Result.fail();
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        Supplier supplier = supplierService.getById(id);
        if(supplier != null) {
            supplier.setIsValid(0);
            return supplierService.updateById(supplier) ? Result.success() : Result.fail();
        }
        return Result.fail();
    }

    @PostMapping("/page")
    public Result page(@RequestBody QueryPageParam query) {
        Page<Supplier> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_valid", 1);
        
        if(query.getParam().get("supplierCode") != null) {
            queryWrapper.like("supplier_code", query.getParam().get("supplierCode"));
        }
        if(query.getParam().get("supplierName") != null) {
            queryWrapper.like("supplier_name", query.getParam().get("supplierName")); 
        }
        if(query.getParam().get("cooperationStatus") != null) {
            queryWrapper.eq("cooperation_status", query.getParam().get("cooperationStatus"));
        }

        IPage<Supplier> result = supplierService.page(page, queryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer supplierId, @RequestParam Integer status) {
        return supplierService.updateStatus(supplierId, status) ? Result.success() : Result.fail();
    }
} 