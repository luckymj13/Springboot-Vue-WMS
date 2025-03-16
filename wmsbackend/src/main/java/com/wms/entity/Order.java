package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    @TableField("order_type")
    private String orderType;     // 1采购/2销售
    @TableField("order_number")
    private String orderNumber;   // 订单编号
    @TableField("creator_id")
    private Integer creatorId;    // 创建人ID
    @TableField("reviewer_id")
    private Integer reviewerId;   // 审核人ID
    @TableField("confirmer_id")
    private Integer confirmerId;  // 确认人ID
    @TableField("related_party_id")
    private Integer relatedPartyId; // 关联客户/供应商ID
    @TableField("related_party_type")
    private String relatedPartyType; // 关联方类型：supplier/customer
    @TableField("status")
    private String status;        // 1待审核/2审核通过/3已确认/4已完成/5已取消/6审核驳回
    @TableField("created_time")
    private LocalDateTime createdTime;
    @TableField("review_time")
    private LocalDateTime reviewTime;
    @TableField("confirm_time")
    private LocalDateTime confirmTime;
    @TableField("complete_time")
    private LocalDateTime completeTime;
    @TableField("total_amount")
    private BigDecimal totalAmount;
    @TableField("remark")
    private String remark;
    @TableField("is_valid")
    private Integer isValid;      // 1有效
    
    @TableField(exist = false)
    private List<OrderDetail> details; // 订单详情，不映射到数据库
    
    @TableField(exist = false)
    private String supplierName; // 供应商名称，不映射到数据库
    
    @TableField(exist = false)
    private String customerName; // 客户名称，不映射到数据库
    
    @TableField(exist = false)
    private String creatorName; // 创建人姓名，不映射到数据库
    
    @TableField(exist = false)
    private String reviewerName; // 审核人姓名，不映射到数据库
    
    @TableField(exist = false)
    private String confirmerName; // 确认人姓名，不映射到数据库
} 