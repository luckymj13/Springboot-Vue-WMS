package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Supplier;
import com.wms.mapper.SupplierMapper;
import com.wms.service.ISupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Override
    public boolean checkCodeExists(String code) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("supplier_code", code)
                   .eq("is_valid", 1);
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Integer supplierId, Integer status) {
        Supplier supplier = getById(supplierId);
        if (supplier != null) {
            supplier.setCooperationStatus(status);
            return updateById(supplier);
        }
        return false;
    }
} 