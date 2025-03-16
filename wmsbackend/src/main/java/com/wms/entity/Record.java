package com.wms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2025-01-30
 */
@TableName("record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 货品id
     */
    @TableField("goods")
    private Integer goods;

    /**
     * 取货人/补货人
     */
    @TableField("userid")
    private Integer userid;

    /**
     * 数量
     */
    @TableField("count")
    private Integer count;

    /**
     * 操作时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 关联订单ID
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 操作类型：0普通出入库/1采购入库/2销售出库/3采购退货/4销售退货
     */
    @TableField("operation_type")
    private Integer operationType;

    /**
     * 0出库/1入库
     */
    @TableField("inandout")
    private Integer inandout;

    @Override
    public String toString() {
        return "Record{" +
            "id=" + id +
            ", goods=" + goods +
            ", userid=" + userid +
            ", count=" + count +
            ", createtime=" + createtime +
            ", remark=" + remark +
            ", orderId=" + orderId +
            ", operationType=" + operationType +
            ", inandout=" + inandout +
        "}";
    }
}
