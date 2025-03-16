package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Customer;

public interface ICustomerService extends IService<Customer> {
    // 检查客户编码是否存在
    boolean checkCodeExists(String code);
    
    // 更新客户等级
    boolean updateLevel(Integer customerId, Integer level);
    
    // 更新信用等级
    boolean updateCreditLevel(Integer customerId, Integer creditLevel);
} 