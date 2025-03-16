package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("order_details")
public class OrderDetail {
    @TableId(type = IdType.AUTO)
    private Integer detailId;
    private Integer orderId;
    private Integer goodsId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String remark;
    @TableField("is_valid")
    private Integer isValid;
    
    @TableField(exist = false)
    private String goodsName; // 商品名称，非数据库字段
} 