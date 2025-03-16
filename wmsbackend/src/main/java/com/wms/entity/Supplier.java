package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("suppliers")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Integer supplierId;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private String bankAccount;
    private Integer creditLevel;  // 1高/2中/3低
    private Integer cooperationStatus; // 1正常/0停用
    private String remark;
    @TableField("is_valid")
    private Integer isValid;
} 