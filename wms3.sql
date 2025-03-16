SET NAMES utf8mb4;
-- --------------------------------------------------------
-- 1) 用户表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `num` VARCHAR(20) NOT NULL COMMENT '账号',
  `name` VARCHAR(255) NOT NULL COMMENT '名字',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `sex` INT NOT NULL COMMENT '1男2女',
  `phone` VARCHAR(255) NOT NULL COMMENT '电话',
  `grade` INT NOT NULL DEFAULT 2 COMMENT '权限 0超级管理员1管理员2普通用户',
  `is_valid` INT DEFAULT 1 COMMENT '是否有效 1有效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  AUTO_INCREMENT=23
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 2) 仓库表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  AUTO_INCREMENT=4
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 3) 出入库记录表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `record`;
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
) ENGINE=InnoDB
  AUTO_INCREMENT=671
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

ALTER TABLE `record`
  ADD COLUMN `order_id` INT DEFAULT NULL COMMENT '关联订单ID',
  ADD COLUMN `operation_type` INT DEFAULT 0 COMMENT '操作类型：0普通出入库/1采购入库/2销售出库/3采购退货/4销售退货',
  ADD COLUMN `inandout` INT NOT NULL DEFAULT 0 COMMENT '0出库/1入库';

-- --------------------------------------------------------
-- 4) 菜单表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `menuCode` VARCHAR(8) DEFAULT NULL COMMENT '菜单编码',
  `menuName` VARCHAR(16) DEFAULT NULL COMMENT '菜单名字',
  `menuLevel` VARCHAR(2) DEFAULT NULL COMMENT '菜单级别',
  `menuParentCode` VARCHAR(8) DEFAULT NULL COMMENT '菜单的父code',
  `menuClick` VARCHAR(16) DEFAULT NULL COMMENT '点击触发的函数',
  `menuRight` VARCHAR(8) DEFAULT NULL COMMENT '权限 0超级管理员 1管理员 2普通用户 可用逗号组合',
  `menuComponent` VARCHAR(200) DEFAULT NULL COMMENT '页面路径',
  `menuIcon` VARCHAR(100) DEFAULT NULL COMMENT '页面图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 12) 插入菜单数据
--    注意：为了避免多行插入冲突，这里用块注释分割
-- --------------------------------------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '001', '管理员管理', '1', NULL, 'Admin', '0', 'admin/AdminManage', 'el-icon-s-custom');
INSERT INTO `menu` VALUES (2, '002', '用户管理', '1 ', NULL, 'User', '0,1', 'user/UserManage', 'el-icon-user-solid');
INSERT INTO `menu` VALUES (3, '003', '仓库管理', '1', NULL, 'Storage', '0,1', 'storage/StorageManage', 'el-icon-office-building');
INSERT INTO `menu` VALUES (4, '004', '物品分类管理', '1', NULL, 'Goodstype', '0,1', 'goodstype/GoodstypeManage', 'el-icon-menu');
INSERT INTO `menu` VALUES (5, '005', '物品管理', '1', NULL, 'Goods', '0,1', 'goods/GoodsManage', 'el-icon-box');
INSERT INTO `menu` VALUES (6, '006', '记录管理', '1', NULL, 'Record', '0,1,2', 'record/RecordManage', 'el-icon-s-order');
INSERT INTO `menu` VALUES (7, '007', '出入库管理', '1', NULL, 'InAndOut', '0,1,2', 'goods/GoodsInAndOut', 'el-icon-pie-chart');
INSERT INTO `menu` VALUES (8, '008', '商品数据', '1', NULL, 'Analysis', '0,1', 'analysis/AnalysisManage', 'el-icon-pie-chart');
INSERT INTO `menu` VALUES (9, '009', '仓库数据', '1', NULL, 'StoreAnalysis', '0,1', 'analysis/StoreAnalysisManage', 'el-icon-date');
-- 添加订单管理、供应商管理、采购管理、客户管理和销售管理的菜单项
INSERT INTO `menu` (`menuCode`, `menuName`, `menuLevel`, `menuParentCode`, `menuClick`, `menuRight`, `menuComponent`, `menuIcon`) 
VALUES ('010', '订单管理', '1', NULL, 'OrderManage', '0,1,2', 'order/OrderManage', 'el-icon-s-order');

INSERT INTO `menu` (`menuCode`, `menuName`, `menuLevel`, `menuParentCode`, `menuClick`, `menuRight`, `menuComponent`, `menuIcon`) 
VALUES ('011', '供应商管理', '1', NULL, 'SupplierManage', '0,1', 'purchase/SupplierManage', 'el-icon-s-cooperation');

INSERT INTO `menu` (`menuCode`, `menuName`, `menuLevel`, `menuParentCode`, `menuClick`, `menuRight`, `menuComponent`, `menuIcon`) 
VALUES ('012', '采购管理', '1', NULL, 'PurchaseManage', '0,1', 'purchase/PurchaseManage', 'el-icon-shopping-cart-full');

INSERT INTO `menu` (`menuCode`, `menuName`, `menuLevel`, `menuParentCode`, `menuClick`, `menuRight`, `menuComponent`, `menuIcon`) 
VALUES ('013', '客户管理', '1', NULL, 'CustomerManage', '0,1', 'sales/CustomerManage', 'el-icon-user');

INSERT INTO `menu` (`menuCode`, `menuName`, `menuLevel`, `menuParentCode`, `menuClick`, `menuRight`, `menuComponent`, `menuIcon`) 
VALUES ('014', '销售管理', '1', NULL, 'SalesManage', '0,1', 'sales/SalesManage', 'el-icon-sold-out');

-- --------------------------------------------------------
-- 5) 物品分类表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `goodstype`;
CREATE TABLE `goodstype` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '分类名',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  AUTO_INCREMENT=5
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 6) 物品表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '货物名称',
  `storage` INT NOT NULL COMMENT '仓库名',
  `goodstype` INT NOT NULL COMMENT '物品分类',
  `count` INT NOT NULL DEFAULT 0 COMMENT '物品数量',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  AUTO_INCREMENT=21
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 7) 分析表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `analysis`;
CREATE TABLE `analysis` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `goodsid` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `value` INT DEFAULT NULL COMMENT '出库数',
  `date` DATE NOT NULL,
  `inandout` INT NOT NULL COMMENT '0出 1入',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB
  AUTO_INCREMENT=614
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 8) 订单表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `order_id` INT NOT NULL AUTO_INCREMENT,
  `order_type` VARCHAR(10) NOT NULL COMMENT '订单类型：1采购/2销售',
  `order_number` VARCHAR(30) NOT NULL COMMENT '订单编号',
  `creator_id` INT NOT NULL COMMENT '创建人ID',
  `reviewer_id` INT DEFAULT NULL COMMENT '审核人ID',
  `confirmer_id` INT DEFAULT NULL COMMENT '确认人ID',
  `related_party_id` INT NOT NULL COMMENT '关联客户/供应商ID',
  `related_party_type` VARCHAR(10) NOT NULL COMMENT '关联方类型：supplier/customer',
  `status` VARCHAR(20) NOT NULL COMMENT '订单状态：1待审核/2审核通过/3已确认/4已完成/5已取消/6审核驳回',
  `created_time` DATETIME NOT NULL,
  `review_time` DATETIME DEFAULT NULL,
  `confirm_time` DATETIME DEFAULT NULL,
  `complete_time` DATETIME DEFAULT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `is_valid` INT DEFAULT 1 COMMENT '是否有效 1有效',
  PRIMARY KEY (`order_id`),
  KEY `idx_order_number` (`order_number`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;
  

-- --------------------------------------------------------
-- 9) 订单明细表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details` (
  `detail_id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `goods_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `is_valid` INT DEFAULT 1 COMMENT '是否有效 1有效',
  PRIMARY KEY (`detail_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 10) 供应商表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers` (
  `supplier_id` INT NOT NULL AUTO_INCREMENT,
  `supplier_code` VARCHAR(20) NOT NULL COMMENT '供应商编码',
  `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账户',
  `credit_level` INT DEFAULT 3 COMMENT '信用等级：1高/2中/3低',
  `cooperation_status` INT DEFAULT 1 COMMENT '合作状态：1正常/0停用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `is_valid` INT DEFAULT 1 COMMENT '是否有效 1有效',
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `idx_supplier_code` (`supplier_code`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------
-- 11) 客户表
-- --------------------------------------------------------
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (
  `customer_id` INT NOT NULL AUTO_INCREMENT,
  `customer_code` VARCHAR(20) NOT NULL COMMENT '客户编码',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账户',
  `credit_level` INT DEFAULT 3 COMMENT '信用等级：1高/2中/3低',
  `customer_level` INT DEFAULT 3 COMMENT '客户级别：1VIP/2重要/3普通',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `is_valid` INT DEFAULT 1 COMMENT '是否有效 1有效',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `idx_customer_code` (`customer_code`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  ROW_FORMAT=DYNAMIC;




-- 小区团购的仓库数据
INSERT INTO `storage` VALUES (1, '中心仓库', '快速配货');
INSERT INTO `storage` VALUES (2, '备用仓库', '存放备用物资');

INSERT INTO `storage` (`id`, `name`, `remark`) VALUES 
(3, '小区自提点A', '专门供小区团购的提货点'),
(4, '团长家车库仓库', '团长自用仓储，适量存放团购商品'),
(5, '社区便利店仓库', '合作便利店提供小型仓储和自提服务');


-- Records of goods
-- ----------------------------


-- 物品分类
INSERT INTO `goodstype` VALUES (1, '食品类', '年货促销专区1');
INSERT INTO `goodstype` VALUES (3, '餐具类', '6666666');
INSERT INTO `goodstype` (`id`, `name`, `remark`) VALUES 
(2, '蔬菜类', '新鲜蔬菜直供'),
(4, '水果类', '当季水果特惠'),
(5, '日用品', '家庭必备用品'),
(6, '零食类', '休闲零食专区'),
(7, '乳制品', '优质牛奶、酸奶'),
(8, '冷冻食品', '速冻食品专区');
INSERT INTO `goodstype` (`id`, `name`, `remark`) VALUES 
(9, '水产类', '新鲜海鲜、水产直供'),
(10, '肉类', '猪肉、牛肉、羊肉等新鲜肉类'),
(11, '调味品', '厨房必备调味料，如酱油、醋、盐等');

-- 商品
INSERT INTO `goods` 
VALUES (1, '可口可乐', 1, 1, 5458, 3.00, '库存小于预计7日库存:1788请及时补货 ! ! !');
INSERT INTO `goods` (`id`, `name`, `storage`, `goodstype`, `count`, `price`, `remark`) VALUES 
(2, '有机白菜', 1, 2, 200, 2.50, '每日新鲜配送'),
(3, '土豆', 1, 2, 500, 3.20, '精选优质土豆'),
(4, '苹果', 1, 4, 300, 5.00, '新鲜富士苹果'),
(5, '香蕉', 1, 4, 400, 4.20, '进口香蕉，口感香甜'),
(6, '抽纸', 2, 5, 100, 6.50, '家庭装抽纸'),
(7, '酸奶', 1, 7, 200, 8.00, '低温酸奶，营养健康'),
(8, '速冻水饺', 1, 8, 150, 12.00, '手工制作速冻水饺'),
(9, '薯片', 2, 6, 250, 7.50, '超值家庭装薯片'),
(10, '牛奶', 1, 7, 300, 9.00, '纯牛奶，每日鲜奶供应'),
(11, '橙子', 1, 4, 250, 5.50, '维C丰富，甜橙优选'),
(12, '黄瓜', 1, 2, 200, 3.80, '新鲜黄瓜，沙拉佳选'),
(13, '鸡蛋', 1, 2, 500, 6.00, '农家散养土鸡蛋'),
(14, '火腿肠', 1, 8, 300, 10.00, '精选肉类，口感香浓'),
(15, '洗衣液', 2, 5, 100, 22.50, '强力去污洗衣液'),
(16, '大米', 2, 5, 500, 50.00, '优质东北大米'),
(17, '食用油', 2, 5, 200, 60.00, '5L桶装食用油'),
(18, '面粉', 2, 5, 300, 35.00, '优质面粉，适合家庭烘焙'),
(19, '鸡肉', 1, 8, 200, 18.00, '冷冻鸡肉，新鲜速达'),
(20, '豆腐', 1, 2, 150, 3.00, '当天新鲜豆腐，适合煲汤');
INSERT INTO `goods` (`id`, `name`, `storage`, `goodstype`, `count`, `price`, `remark`) VALUES 
(21, '新鲜三文鱼', 1, 9, 120, 45.00, '进口三文鱼刺身级，需冷藏'),
(22, '精选猪肉五花', 1, 10, 200, 38.00, '优质猪肉五花，适合炖煮'),
(23, '有机酱油', 2, 11, 300, 15.50, '天然发酵酱油，口感醇厚');

-- 出入库记录
INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `order_id`, `operation_type`, `inandout`) VALUES 
(2, 3, 100, '2025-03-01 08:30:00', '采购入库', 3, 1, 1),
(3, 2, 200, '2025-03-01 09:00:00', '采购入库', 3, 1, 1),
(4, 2, 150, '2025-03-02 10:00:00', '采购入库', 4, 1, 1),
(5, 3, 100, '2025-03-02 11:30:00', '销售出库', 5, 2, 0),
(6, 2, 50, '2025-03-03 15:00:00', '销售出库', 6, 2, 0),
(7, 3, 70, '2025-03-03 16:00:00', '采购入库', 7, 1, 1),
(8, 2, 80, '2025-03-04 09:00:00', '销售出库', 8, 2, 0);

-- 插入订单数据
INSERT INTO `orders` (`order_type`, `order_number`, `creator_id`, `reviewer_id`, `confirmer_id`, `related_party_id`, `related_party_type`, `status`, `created_time`, `review_time`, `confirm_time`, `complete_time`, `total_amount`, `remark`, `is_valid`) VALUES 
('1', 'PO20250311001', 4, 6, 7, 5, 'supplier', '4', '2025-03-11 08:30:00', '2025-03-11 09:00:00', '2025-03-11 10:00:00', '2025-03-12 12:00:00', 3200.00, '采购蔬菜水果', 1),
('1', 'PO20250312002', 5, 6, 8, 6, 'supplier', '4', '2025-03-12 10:30:00', '2025-03-12 11:00:00', '2025-03-12 12:00:00', '2025-03-13 15:00:00', 2800.00, '采购生活用品', 1),
('2', 'SO20250313001', 6, 7, 9, 5, 'customer', '3', '2025-03-13 15:30:00', '2025-03-13 16:00:00', '2025-03-13 17:00:00', NULL, 1500.00, '社区团购订单', 1),
('2', 'SO20250314002', 7, 8, 10, 6, 'customer', '2', '2025-03-14 14:00:00', '2025-03-14 14:30:00', NULL, NULL, 1800.00, '居民拼单', 1);


-- 插入订单明细数据
INSERT INTO `order_details` (`order_id`, `goods_id`, `quantity`, `unit_price`, `total_price`, `remark`, `is_valid`) VALUES 
(1, 2, 100, 2.50, 250.00, '采购白菜', 1),
(1, 4, 150, 5.00, 750.00, '采购苹果', 1),
(1, 5, 120, 4.20, 504.00, '采购香蕉', 1),
(2, 6, 50, 6.50, 325.00, '采购抽纸', 1),
(2, 7, 80, 8.00, 640.00, '采购酸奶', 1),
(3, 8, 60, 12.00, 720.00, '团购水饺', 1),
(3, 9, 50, 7.50, 375.00, '团购薯片', 1),
(3, 10, 40, 9.00, 360.00, '团购牛奶', 1),
(4, 11, 70, 5.50, 385.00, '拼单橙子', 1),
(4, 12, 100, 3.80, 380.00, '拼单黄瓜  ', 1),
(4, 13, 90, 6.00, 540.00, '拼单鸡蛋', 1);

-- 供应商数据
INSERT INTO `suppliers` (`supplier_code`, `supplier_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `cooperation_status`, `remark`, `is_valid`) VALUES 
('SUP003', '绿园蔬菜供应', '刘先生', '13612345678', '上海市浦东新区蔬菜市场', '6225 0000 1111 2222', 1, 1, '长期合作供应商', 1),
('SUP004', '新鲜水果配送', '赵女士', '13598765432', '深圳市南山区果品批发市场', '6226 2222 3333 4444', 2, 1, '稳定水果供应', 1);
INSERT INTO `suppliers` (`supplier_code`, `supplier_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `cooperation_status`, `remark`, `is_valid`) VALUES 
('SUP005', '渔家海鲜供应链', '张渔民', '13688885555', '山东青岛海鲜市场', '6225 8888 9999 0000', 1, 1, '长期供应优质海鲜', 1),
('SUP006', '华南生鲜批发', '李供应', '13777776666', '广东广州生鲜配送中心', '6226 1111 2222 3333', 2, 1, '优质果蔬直供商', 1),
('SUP007', '调味品工厂直供', '王经理', '13599998888', '江苏苏州调味品生产基地', '6227 4444 5555 6666', 1, 1, '提供高质量调味品', 1);
INSERT INTO `suppliers` (`supplier_code`, `supplier_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `cooperation_status`, `remark`, `is_valid`) VALUES 
('SUP008', '天天蔬菜批发', '王老板', '13988887777', '江苏省南京市农贸市场C区12号', '6225 8888 7777 6666', 1, 1, '本地新鲜蔬菜直供', 1),
('SUP009', '优质水果供应', '李经理', '13766665555', '浙江省杭州市水果批发市场A栋5层', '6226 9999 8888 7777', 2, 1, '每日新鲜水果配送', 1),
('SUP010', '家庭日用品配送', '赵女士', '13855554444', '广东省深圳市龙华区仓库7号', '6227 3333 2222 1111', 1, 1, '提供纸巾、洗涤用品等', 1);

-- 客户数据
INSERT INTO `customers` (`customer_code`, `customer_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `customer_level`, `remark`, `is_valid`) VALUES 
('CUS003', '社区团购平台A', '张先生', '13899998888', '上海市静安区社区团购中心', '6227 4444 5555 6666', 1, 1, '稳定合作客户', 1),
('CUS004', '团购便利店', '李女士', '13777776666', '深圳市福田区团购批发商', '6229 6666 7777 8888', 2, 2, '重要零售商客户', 1);
INSERT INTO `customers` (`customer_code`, `customer_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `customer_level`, `remark`, `is_valid`) VALUES 
('CUS005', '上海社区团购', '赵团长', '13855557777', '上海市浦东新区社区团购中心', '6228 7777 8888 9999', 1, 1, '社区团长直销合作', 1),
('CUS006', '北京连锁超市', '钱老板', '13666669999', '北京市朝阳区大型超市配送中心', '6229 1111 2222 3333', 2, 2, '连锁超市长期客户', 1),
('CUS007', '广州生鲜便利店', '孙店长', '13544445555', '广州市天河区生鲜连锁店', '6230 3333 4444 5555', 3, 3, '主要采购生鲜食品', 1);
INSERT INTO `customers` (`customer_code`, `customer_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `customer_level`, `remark`, `is_valid`) VALUES 
('CUS008', '幸福花园团长', '张团长', '13544443333', '上海市浦东新区幸福花园小区12栋', '6228 2222 3333 4444', 1, 1, '小区团购组织者', 1),
('CUS009', '家家乐便利店', '陈店长', '13677778888', '北京市朝阳区社区商铺B1', '6229 4444 5555 6666', 2, 2, '社区团购提货点', 1),
('CUS010', '邻里生鲜超市', '孙老板', '13499990000', '广州市天河区邻里社区商业中心', '6230 6666 7777 8888', 3, 3, '长期合作客户', 1);

-- 用户表    
INSERT INTO `user` VALUES (1, 'SuperAdmin', '超级管理员', '123456', 20, 1, '13395874554', 0, 1);
INSERT INTO `user` VALUES (2, 'admin', '管理员', 'admin', 21, 1, '13432343454', 1, 1);
INSERT INTO `user` VALUES (3, 'shanshan', '程序员山山', '123456', 28, 1, '13423490456', 2, 1);
INSERT INTO `user` (`id`, `num`, `name`, `password`, `age`, `sex`, `phone`, `grade`, `is_valid`) VALUES 
(4, 'user001', '张三', 'pass123', 35, 1, '13888880001', 2, 1),
(5, 'user002', '李四', 'pass234', 28, 1, '13888880002', 2, 1),
(6, 'user003', '王五', 'pass345', 30, 2, '13888880003', 1, 1),
(7, 'user004', '赵六', 'pass456', 32, 1, '13888880004', 1, 1),
(8, 'user005', '孙七', 'pass567', 27, 2, '13888880005', 2, 1),
(9, 'user006', '周八', 'pass678', 40, 1, '13888880006', 1, 1),
(10, 'user007', '吴九', 'pass789', 25, 2, '13888880007', 2, 1),
(11, 'user008', '郑十', 'pass890', 29, 1, '13888880008', 1, 1),
(12, 'user009', '黄十一', 'pass901', 33, 2, '13888880009', 2, 1),
(13, 'user010', '何十二', 'pass012', 26, 1, '13888880010', 2, 1);


-- 插入分析数据，记录不同商品的出库和入库情况
INSERT INTO `analysis` (`id`, `goodsid`, `name`, `value`, `date`, `inandout`) VALUES 
(10, 2, '有机白菜', 120, '2025-03-01', 0),  -- 出库
(11, 3, '土豆', 200, '2025-03-01', 1),  -- 入库
(12, 4, '苹果', 150, '2025-03-02', 1),  -- 入库
(13, 5, '香蕉', 100, '2025-03-02', 0),  -- 出库
(14, 6, '抽纸', 50, '2025-03-03', 0),  -- 出库
(15, 7, '酸奶', 70, '2025-03-03', 1),  -- 入库
(16, 8, '速冻水饺', 80, '2025-03-04', 0),  -- 出库
(17, 9, '薯片', 90, '2025-03-04', 1),  -- 入库
(18, 10, '牛奶', 120, '2025-03-05', 0),  -- 出库
(19, 11, '橙子', 140, '2025-03-05', 1),  -- 入库
(20, 12, '黄瓜', 60, '2025-03-06', 0),  -- 出库
(21, 13, '鸡蛋', 200, '2025-03-06', 1),  -- 入库
(22, 14, '火腿肠', 80, '2025-03-07', 0),  -- 出库
(23, 15, '洗衣液', 40, '2025-03-07', 1),  -- 入库
(24, 16, '大米', 150, '2025-03-08', 0),  -- 出库
(25, 17, '食用油', 100, '2025-03-08', 1),  -- 入库
(26, 18, '面粉', 130, '2025-03-09', 0),  -- 出库
(27, 19, '鸡肉', 160, '2025-03-09', 1),  -- 入库
(28, 20, '豆腐', 90, '2025-03-10', 0);  -- 出库






-- 插入 30 条出入库记录数据
INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `order_id`, `operation_type`, `inandout`) VALUES 
(2, 4, 50, '2025-03-01 08:30:00', '采购白菜入库', 1, 1, 1),
(3, 5, 100, '2025-03-01 09:00:00', '采购土豆入库', 1, 1, 1),
(4, 6, 80, '2025-03-02 10:00:00', '采购苹果入库', 2, 1, 1),
(5, 7, 60, '2025-03-02 11:30:00', '采购香蕉入库', 2, 1, 1),
(6, 8, 40, '2025-03-03 15:00:00', '采购抽纸入库', 3, 1, 1),
(7, 9, 90, '2025-03-03 16:00:00', '采购酸奶入库', 3, 1, 1),
(8, 10, 100, '2025-03-04 09:00:00', '速冻水饺入库', 4, 1, 1),
(9, 4, 50, '2025-03-04 10:30:00', '薯片入库', 4, 1, 1),
(10, 5, 120, '2025-03-05 08:00:00', '牛奶采购入库', 5, 1, 1),
(11, 6, 70, '2025-03-05 10:00:00', '橙子入库', 5, 1, 1),

(2, 7, 30, '2025-03-06 12:00:00', '小区团购白菜出库', 6, 2, 0),
(3, 8, 50, '2025-03-06 13:00:00', '小区团购土豆出库', 6, 2, 0),
(4, 9, 40, '2025-03-07 14:00:00', '苹果团购出库', 7, 2, 0),
(5, 10, 30, '2025-03-07 15:00:00', '香蕉团购出库', 7, 2, 0),
(6, 4, 20, '2025-03-08 09:30:00', '抽纸团购出库', 8, 2, 0),
(7, 5, 40, '2025-03-08 10:00:00', '酸奶团购出库', 8, 2, 0),
(8, 6, 50, '2025-03-09 11:30:00', '水饺团购出库', 9, 2, 0),
(9, 7, 30, '2025-03-09 12:30:00', '薯片团购出库', 9, 2, 0),
(10, 8, 80, '2025-03-10 13:45:00', '牛奶团购出库', 10, 2, 0),
(11, 9, 50, '2025-03-10 14:30:00', '橙子团购出库', 10, 2, 0),

(2, 10, 40, '2025-03-11 08:00:00', '社区库存补充白菜', NULL, 0, 1),
(3, 4, 70, '2025-03-11 09:00:00', '社区库存补充土豆', NULL, 0, 1),
(4, 5, 60, '2025-03-12 10:00:00', '社区库存补充苹果', NULL, 0, 1),
(5, 6, 50, '2025-03-12 11:30:00', '社区库存补充香蕉', NULL, 0, 1),
(6, 7, 30, '2025-03-13 14:00:00', '社区库存补充抽纸', NULL, 0, 1),
(7, 8, 60, '2025-03-13 15:30:00', '社区库存补充酸奶', NULL, 0, 1),
(8, 9, 80, '2025-03-14 16:30:00', '社区库存补充水饺', NULL, 0, 1),
(9, 10, 50, '2025-03-14 17:30:00', '社区库存补充薯片', NULL, 0, 1),
(10, 4, 100, '2025-03-15 18:00:00', '社区库存补充牛奶', NULL, 0, 1),
(11, 5, 70, '2025-03-15 19:00:00', '社区库存补充橙子', NULL, 0, 1);




INSERT INTO `goods` (`id`, `name`, `storage`, `goodstype`, `count`, `price`, `remark`) VALUES
(24, '方便面', 1, 1, 300, 4.50, '经典方便面，快速美味'),
(25, '火锅底料', 1, 1, 150, 15.00, '麻辣火锅底料，地道风味'),
(26, '米粉', 1, 1, 200, 8.50, '米制食品，适合煮炒'),
(27, '面包', 1, 1, 250, 10.00, '松软面包，早餐好选择'),
(28, '豆浆粉', 1, 1, 180, 12.00, '营养豆浆粉，早餐必备');


INSERT INTO `analysis` (`id`, `goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(29, 24, '方便面', 100, '2025-03-01', 1),  -- 入库
(30, 25, '火锅底料', 50, '2025-03-02', 0),  -- 出库
(31, 26, '米粉', 80, '2025-03-03', 1),      -- 入库
(32, 27, '面包', 60, '2025-03-04', 0),      -- 出库
(33, 28, '豆浆粉', 90, '2025-03-05', 1);    -- 入库



INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `order_id`, `operation_type`, `inandout`) VALUES
(24, 6, 50, '2025-03-01 09:00:00', '采购方便面入库', 11, 1, 1),
(25, 7, 40, '2025-03-02 10:00:00', '火锅底料团购出库', 12, 2, 0),
(26, 8, 60, '2025-03-03 14:00:00', '米粉入库', 13, 1, 1),
(27, 9, 30, '2025-03-04 16:00:00', '面包团购出库', 14, 2, 0),
(28, 10, 70, '2025-03-05 18:00:00', '豆浆粉入库', 15, 1, 1);


INSERT INTO `analysis` (`id`, `goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(34, 24, '方便面', 80, '2025-03-06', 0),   -- 出库
(35, 25, '火锅底料', 100, '2025-03-07', 1), -- 入库
(36, 26, '米粉', 50, '2025-03-08', 0),     -- 出库
(37, 27, '面包', 150, '2025-03-09', 1),    -- 入库
(38, 28, '豆浆粉', 60, '2025-03-10', 0),   -- 出库
(39, 24, '方便面', 120, '2025-03-11', 1),  -- 入库
(40, 25, '火锅底料', 40, '2025-03-12', 0), -- 出库
(41, 26, '米粉', 100, '2025-03-13', 1),    -- 入库
(42, 27, '面包', 30, '2025-03-14', 0),     -- 出库
(43, 28, '豆浆粉', 100, '2025-03-15', 1);  -- 入库

INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `order_id`, `operation_type`, `inandout`) VALUES
(24, 11, 40, '2025-03-06 09:30:00', '社区库存补充方便面', NULL, 0, 1),
(25, 12, 60, '2025-03-07 11:00:00', '火锅底料入库补充', NULL, 0, 1),
(26, 13, 30, '2025-03-08 13:00:00', '米粉社区团购出库', NULL, 0, 0),
(27, 14, 80, '2025-03-09 15:30:00', '面包超市补货入库', NULL, 0, 1),
(28, 15, 50, '2025-03-10 17:00:00', '豆浆粉社区团购出库', NULL, 0, 0),
(24, 16, 100, '2025-03-11 09:30:00', '方便面超市库存补充', NULL, 0, 1),
(25, 17, 30, '2025-03-12 11:00:00', '火锅底料社区团购出库', NULL, 0, 0),
(26, 18, 70, '2025-03-13 14:00:00', '米粉批量采购入库', NULL, 0, 1),
(27, 19, 20, '2025-03-14 16:00:00', '面包社区团购出库', NULL, 0, 0),
(28, 20, 90, '2025-03-15 18:30:00', '豆浆粉超市补货入库', NULL, 0, 1);




----------------------


-- 为方便面(ID=24)添加连续8天的出库记录
INSERT INTO `analysis` (`goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(24, '方便面', 80, '2025-03-06', 0),   -- 出库
(24, '方便面', 85, '2025-03-07', 0),   -- 出库
(24, '方便面', 90, '2025-03-08', 0),   -- 出库
(24, '方便面', 95, '2025-03-09', 0),   -- 出库
(24, '方便面', 100, '2025-03-10', 0),  -- 出库
(24, '方便面', 105, '2025-03-11', 0),  -- 出库
(24, '方便面', 110, '2025-03-12', 0);  -- 出库

-- 为火锅底料(ID=25)添加连续8天的出库记录
INSERT INTO `analysis` (`goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(25, '火锅底料', 45, '2025-03-03', 0),   -- 出库
(25, '火锅底料', 48, '2025-03-04', 0),   -- 出库
(25, '火锅底料', 52, '2025-03-05', 0),   -- 出库
(25, '火锅底料', 55, '2025-03-06', 0),   -- 出库
(25, '火锅底料', 58, '2025-03-07', 0),   -- 出库
(25, '火锅底料', 62, '2025-03-08', 0),   -- 出库
(25, '火锅底料', 65, '2025-03-09', 0);   -- 出库

-- 为米粉(ID=26)添加连续8天的出库记录
INSERT INTO `analysis` (`goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(26, '米粉', 70, '2025-03-04', 0),   -- 出库
(26, '米粉', 72, '2025-03-05', 0),   -- 出库
(26, '米粉', 75, '2025-03-06', 0),   -- 出库
(26, '米粉', 78, '2025-03-07', 0),   -- 出库
(26, '米粉', 80, '2025-03-08', 0),   -- 出库
(26, '米粉', 82, '2025-03-09', 0),   -- 出库
(26, '米粉', 85, '2025-03-10', 0);   -- 出库

-- 为面包(ID=27)添加连续8天的出库记录
INSERT INTO `analysis` (`goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(27, '面包', 65, '2025-03-05', 0),   -- 出库
(27, '面包', 68, '2025-03-06', 0),   -- 出库
(27, '面包', 70, '2025-03-07', 0),   -- 出库
(27, '面包', 72, '2025-03-08', 0),   -- 出库
(27, '面包', 75, '2025-03-09', 0),   -- 出库
(27, '面包', 78, '2025-03-10', 0),   -- 出库
(27, '面包', 80, '2025-03-11', 0);   -- 出库

-- 为豆浆粉(ID=28)添加连续8天的出库记录
INSERT INTO `analysis` (`goodsid`, `name`, `value`, `date`, `inandout`) VALUES
(28, '豆浆粉', 60, '2025-03-06', 0),   -- 出库
(28, '豆浆粉', 62, '2025-03-07', 0),   -- 出库
(28, '豆浆粉', 65, '2025-03-08', 0),   -- 出库
(28, '豆浆粉', 68, '2025-03-09', 0),   -- 出库
(28, '豆浆粉', 70, '2025-03-10', 0),   -- 出库
(28, '豆浆粉', 72, '2025-03-11', 0),   -- 出库
(28, '豆浆粉', 75, '2025-03-12', 0);   -- 出库

-- 添加对应的出库记录到record表
INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `operation_type`, `inandout`) VALUES
-- 方便面出库记录
(24, 1, 80, '2025-03-06 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 85, '2025-03-07 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 90, '2025-03-08 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 95, '2025-03-09 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 100, '2025-03-10 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 105, '2025-03-11 09:30:00', '方便面社区团购出库', 2, 0),
(24, 1, 110, '2025-03-12 09:30:00', '方便面社区团购出库', 2, 0),

-- 火锅底料出库记录
(25, 1, 45, '2025-03-03 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 48, '2025-03-04 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 52, '2025-03-05 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 55, '2025-03-06 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 58, '2025-03-07 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 62, '2025-03-08 10:00:00', '火锅底料社区团购出库', 2, 0),
(25, 1, 65, '2025-03-09 10:00:00', '火锅底料社区团购出库', 2, 0),

-- 米粉出库记录
(26, 1, 70, '2025-03-04 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 72, '2025-03-05 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 75, '2025-03-06 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 78, '2025-03-07 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 80, '2025-03-08 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 82, '2025-03-09 11:00:00', '米粉社区团购出库', 2, 0),
(26, 1, 85, '2025-03-10 11:00:00', '米粉社区团购出库', 2, 0),

-- 面包出库记录
(27, 1, 65, '2025-03-05 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 68, '2025-03-06 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 70, '2025-03-07 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 72, '2025-03-08 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 75, '2025-03-09 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 78, '2025-03-10 12:00:00', '面包社区团购出库', 2, 0),
(27, 1, 80, '2025-03-11 12:00:00', '面包社区团购出库', 2, 0),

-- 豆浆粉出库记录
(28, 1, 60, '2025-03-06 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 62, '2025-03-07 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 65, '2025-03-08 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 68, '2025-03-09 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 70, '2025-03-10 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 72, '2025-03-11 13:00:00', '豆浆粉社区团购出库', 2, 0),
(28, 1, 75, '2025-03-12 13:00:00', '豆浆粉社区团购出库', 2, 0);




INSERT INTO `suppliers` (`supplier_id`, `supplier_code`, `supplier_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `cooperation_status`, `remark`) VALUES
(9, 'SUP011', '北京食品供应有限公司', '张经理', '13800138001', '北京市朝阳区食品工业园区A12号', '6225880137945210', 1, 1, '主要提供方便面和火锅底料'),
(10, 'SUP012', '广州粮油批发中心', '李总', '13900139002', '广州市白云区粮油市场B区23号', '6225880137945211', 1, 1, '主要提供米粉和豆浆粉'),
(11, 'SUP013', '上海面包工坊', '王师傅', '13700137003', '上海市松江区工业园C路18号', '6225880137945212', 2, 1, '专业面包供应商'),
(12, 'SUP014', '成都调味品有限公司', '赵经理', '13600136004', '成都市武侯区调味品产业园D栋', '6225880137945213', 2, 1, '各类调味品供应商'),
(13, 'SUP015', '深圳食品贸易有限公司', '陈总', '13500135005', '深圳市宝安区物流园区E座', '6225880137945214', 1, 1, '综合食品供应商');


INSERT INTO `customers` (`customer_id`, `customer_code`, `customer_name`, `contact_person`, `contact_phone`, `address`, `bank_account`, `credit_level`, `customer_level`, `remark`) VALUES
(9, 'CUS011', '星城社区团购中心', '刘经理', '13800138001', '长沙市岳麓区星城社区', '6225880137945220', 1, 1, '每周固定采购'),
(10, 'CUS012', '阳光小区团购站', '孙站长', '13900139002', '北京市海淀区阳光小区', '6225880137945221', 1, 3, '双周采购一次'),
(11, 'CUS013', '绿茵花园社区店', '钱店长', '13700137003', '上海市浦东新区绿茵花园', '6225880137945222', 2, 1, '大型社区店'),
(12, 'CUS014', '幸福里便利店', '周经理', '13600136004', '广州市天河区幸福里小区', '6225880137945223', 3, 3, '小型便利店'),
(13, 'CUS015', '蓝天社区团购点', '吴站长', '13500135005', '深圳市南山区蓝天社区', '6225880137945224', 2, 1, '新开发客户');


INSERT INTO `orders` (`order_id`, `order_type`, `order_number`, `creator_id`, `reviewer_id`, `confirmer_id`, `related_party_id`, `related_party_type`, `status`, `created_time`, `review_time`, `confirm_time`, `complete_time`, `total_amount`, `remark`) VALUES
(5, '采购', 'PO202503150001', 2, 3, 1, 9, 'supplier', '已完成', '2025-03-15 09:00:00', '2025-03-15 10:30:00', '2025-03-15 14:00:00', '2025-03-16 11:00:00', 2250.00, '方便面和火锅底料采购'),
(6, '采购', 'PO202503160001', 2, 3, 1, 10, 'supplier', '已确认', '2025-03-16 09:30:00', '2025-03-16 11:00:00', '2025-03-16 15:30:00', NULL, 2550.00, '米粉和豆浆粉采购'),
(7, '采购', 'PO202503170001', 2, 3, NULL, 11, 'supplier', '审核通过', '2025-03-17 10:00:00', '2025-03-17 14:00:00', NULL, NULL, 2500.00, '面包采购');


INSERT INTO `order_details` (`detail_id`, `order_id`, `goods_id`, `quantity`, `unit_price`, `total_price`, `remark`) VALUES
(12, 5, 24, 300, 4.50, 1350.00, '方便面采购'),
(13, 5, 25, 60, 15.00, 900.00, '火锅底料采购'),
(14, 6, 26, 200, 8.50, 1700.00, '米粉采购'),
(15, 6, 28, 70, 12.00, 840.00, '豆浆粉采购');


INSERT INTO `record` (`goods`, `userid`, `count`, `createtime`, `remark`, `order_id`, `operation_type`, `inandout`) VALUES
(24, 1, 300, '2025-03-16 11:00:00', '方便面采购入库', 5, 1, 1),
(25, 1, 60, '2025-03-16 11:30:00', '火锅底料采购入库', 5, 1, 1),
(24, 1, 200, '2025-03-16 09:00:00', '方便面销售出库', 6, 2, 0),
(25, 1, 40, '2025-03-16 09:30:00', '火锅底料销售出库', 6, 2, 0);
