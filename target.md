我有一个基于Spring Boot+Vue.js的社区团购仓库管理系统，现有功能可以正常运行，在不影响我现有功能的前提下，尽量不改动我的原有代码，现在需要扩展三个核心模块：订单审核模块、采购管理模块和销售管理模块。保持与原代码的一致性，后端技术一致，前端ui一致，请帮我实现这些功能模块的代码，并整合到现有系统中。

## 现有系统架构概述

1. 前端：Vue.js + Vue Router + Vuex + Axios + Echarts
2. 后端：Spring Boot + Spring Data JPA + JWT
3. 数据库：MySQL 8.0.25

## 新增功能需求

### 1. 订单审核模块
- 实现订单创建、查询、审核、确认的完整流程
- 支持多级审核机制(提交→一级审核→二级审核→确认)
- 完整的订单状态流转

### 2. 采购管理模块
- 供应商信息管理
- 采购订单创建和管理
- 采购入库与退货处理
- 与现有入库功能集成

### 3. 销售管理模块
- 客户信息管理
- 销售订单创建和管理
- 销售出库与退货处理
- 与现有出库功能集成

## 代码修改任务

### 任务1：读取数据库表

1. 订单表(orders)，包含以下字段：
   - order_id(主键)
   - order_type(订单类型：采购/销售)
   - order_number(订单编号)
   - creator_id(创建人ID)
   - reviewer_id(审核人ID)
   - confirmer_id(确认人ID)
   - related_party_id(关联客户/供应商ID)
   - status(订单状态：待审核/审核通过/已确认/已完成/已取消/审核驳回)
   - created_time, review_time, confirm_time, complete_time
   - total_amount
   - remark

2. 订单明细表(order_details)，包含以下字段：
   - detail_id(主键)
   - order_id(订单ID，外键)
   - item_id(物品ID，外键)
   - quantity(数量)
   - unit_price(单价)
   - total_price(总价)
   - remark

3. 供应商表(suppliers)，包含以下字段：
   - supplier_id(主键)
   - supplier_code(供应商编码)
   - supplier_name(供应商名称)
   - contact_person(联系人)
   - contact_phone(联系电话)
   - address
   - bank_account
   - credit_level
   - cooperation_status
   - remark

4. 客户表(customers)，包含以下字段：
   - customer_id(主键)
   - customer_code(客户编码)
   - customer_name(客户名称)
   - contact_person(联系人)
   - contact_phone(联系电话)
   - address
   - bank_account
   - credit_level
   - customer_level
   - remark

5. 记录表(records)，新增：
   - order_id(关联订单ID)
   - operation_type(操作类型：普通出入库/采购入库/销售出库/采购退货/销售退货)
   
   CREATE TABLE `record` (
   
    `id` INT NOT NULL AUTO_INCREMENT,
   
    `goods` INT NOT NULL COMMENT '货品id',
   
    `userid` INT NOT NULL COMMENT '取货人/补货人',
   
    `count` INT NOT NULL COMMENT '数量',
   
    `createtime` TIMESTAMP NULL 
   
      DEFAULT CURRENT_TIMESTAMP 
   
      ON UPDATE CURRENT_TIMESTAMP 
   
      COMMENT '操作时间',
   
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
   
    PRIMARY KEY (`id`)
   
   ) 
   
   ALTER TABLE `record`
   
    ADD COLUMN `order_id` INT DEFAULT NULL COMMENT '关联订单ID',
   
    ADD COLUMN `operation_type` INT DEFAULT 0 COMMENT '操作类型：0普通出入库/1采购入库/2销售出库/3采购退货/4销售退货',
   
    ADD COLUMN `inandout` INT NOT NULL DEFAULT 0 COMMENT '0出库/1入库';

### 任务2：后端实现

1. 创建实体类：
   - Order.java
   - OrderDetail.java
   - Supplier.java
   - Customer.java
   - 更新Record.java

2. 创建Repository接口：
   - OrderRepository
   - OrderDetailRepository
   - SupplierRepository
   - CustomerRepository

3. 创建Service接口及实现：
   - OrderService/OrderServiceImpl
   - SupplierService/SupplierServiceImpl
   - CustomerService/CustomerServiceImpl
   - PurchaseService/PurchaseServiceImpl
   - SalesService/SalesServiceImpl

4. 创建Controller：
   - OrderController
   - SupplierController
   - CustomerController
   - PurchaseController
   - SalesController

5. 业务逻辑实现：
   - 订单审核流程：创建→审核→确认
   - 采购流程：订单确认→入库→库存更新
   - 销售流程：订单确认→出库→库存更新
   - 库存与订单的关联逻辑

### 任务3：前端实现

1. 路由配置修改：
   - 在router/index.js中添加订单管理、采购管理、销售管理路由
   - 按角色配置路由访问权限

2. Vuex状态管理扩展：
   - 新增order.js、supplier.js、customer.js等状态模块
   - 实现各模块的状态管理和操作方法

3. 页面组件开发：
   - 订单管理相关页面(创建、列表、审核、详情)
   - 供应商管理页面
   - 客户管理页面
   - 采购管理相关页面
   - 销售管理相关页面

4. 现有组件修改：
   - 出入库组件扩展，支持与订单关联
   - 库存组件调整，支持新的业务流程

## 具体实现指南

### 订单审核流程实现

1. 订单创建：
   - 用户填写订单基本信息(类型、客户/供应商、备注等)
   - 添加订单明细(物品、数量、单价)
   - 自动计算总金额
   - 提交后状态设为"待审核"

2. 订单审核：
   - 管理员查看待审核订单
   - 进行一级审核，通过则状态改为"审核通过"
   - 超级管理员进行二级审核，通过则状态改为"已确认"

3. 订单执行：
   - 采购订单确认后，进入采购流程
   - 销售订单确认后，进入销售流程

### 采购流程实现

1. 供应商管理：
   - CRUD操作实现
   - 供应商信息维护

2. 采购订单处理：
   - 订单确认后，联系供应商
   - 物品到达后，验收入库
   - 生成采购入库记录，关联订单
   - 更新物品库存
   - 订单状态变更为"已完成"

### 销售流程实现

1. 客户管理：
   - CRUD操作实现
   - 客户信息维护

2. 销售订单处理：
   - 订单确认后，检查库存
   - 库存充足则执行出库
   - 生成销售出库记录，关联订单
   - 更新物品库存
   - 订单状态变更为"已完成"

## 代码示例期望

请为我实现上述功能的核心代码，包括但不限于：

6. 前端Vuex状态管理模块
7. 前端路由配置
8. 关键页面组件代码

请确保代码符合当前系统的技术栈和风格，并尽可能做到模块化、可维护性高。