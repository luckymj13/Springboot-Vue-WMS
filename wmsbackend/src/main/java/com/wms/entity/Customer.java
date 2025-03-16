package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customers")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Integer customerId;
    private String customerCode;
    private String customerName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private String bankAccount;
    private Integer creditLevel;  // 1高/2中/3低
    private Integer customerLevel; // 1VIP/2重要/3普通
    private String remark;
    @TableField("is_valid")
    private Integer isValid;
} 