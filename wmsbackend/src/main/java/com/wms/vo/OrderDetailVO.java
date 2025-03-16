package com.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailVO {
    private Integer detailId;
    private Integer orderId;
    private Integer goodsId;
    private String goodsName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String remark;
} 