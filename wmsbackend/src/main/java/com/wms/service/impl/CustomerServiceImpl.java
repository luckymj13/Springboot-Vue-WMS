package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Customer;
import com.wms.mapper.CustomerMapper;
import com.wms.service.ICustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    @Override
    public boolean checkCodeExists(String code) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_code", code)
                   .eq("is_valid", 1);
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public boolean updateLevel(Integer customerId, Integer level) {
        Customer customer = getById(customerId);
        if (customer != null) {
            customer.setCustomerLevel(level);
            return updateById(customer);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateCreditLevel(Integer customerId, Integer creditLevel) {
        Customer customer = getById(customerId);
        if (customer != null) {
            customer.setCreditLevel(creditLevel);
            return updateById(customer);
        }
        return false;
    }
} 