package com.wms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordRes extends Record {
    private String username;
    private String goodsname;
    private String storagename;
    private String goodstypename;

    @Override
    public String toString() {
        return "RecordRes{" +
            "id=" + getId() +
            ", goods=" + getGoods() +
            ", userid=" + getUserid() +
            ", count=" + getCount() +
            ", createtime=" + getCreatetime() +
            ", remark=" + getRemark() +
            ", orderId=" + getOrderId() +
            ", operationType=" + getOperationType() +
            ", inandout=" + getInandout() +
            ", username='" + username + '\'' +
            ", goodsname='" + goodsname + '\'' +
            ", storagename='" + storagename + '\'' +
            ", goodstypename='" + goodstypename + '\'' +
            '}';
    }
}
