-- 使用junk_shop数据库
USE junk_shop;

-- 添加商品数量字段到订单表
ALTER TABLE `orders` ADD COLUMN `Quantity` int NOT NULL DEFAULT 1 AFTER `ItemName`; 