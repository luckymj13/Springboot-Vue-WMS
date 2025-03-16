package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Supplier;

public interface ISupplierService extends IService<Supplier> {
    // 检查供应商编码是否存在
    boolean checkCodeExists(String code);
    
    // 更新合作状态
    boolean updateStatus(Integer supplierId, Integer status);
} 