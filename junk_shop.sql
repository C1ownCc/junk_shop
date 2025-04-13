/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : junk_shop

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 13/04/2025 21:35:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `AnnouncementID` int(0) NOT NULL AUTO_INCREMENT,
  `Content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`AnnouncementID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '# 二手交易平台系统公告\n\n## 重要更新通知 🎉\n\n**亲爱的用户们：**\n\n*欢迎使用二手交易平台！* 我们很高兴地通知您，平台已完成重大更新。\n\n---\n\n### 1. 新功能上线\n\n- [x] AI智能助手上线\n- [x] 实时聊天系统优化\n- [ ] 支付系统升级（进行中）\n- [ ] 移动端适配（计划中）\n\n### 2. 安全提醒\n\n> 请注意：为保障交易安全，请务必在平台内进行交易，切勿线下交易！\n\n### 3. 交易流程\n\n1. 选择商品\n2. 联系卖家\n3. 确认交易\n4. 支付订单\n5. 确认收货\n\n### 4. 代码示范\n\n如果遇到页面显示问题，请尝试以下代码：\n```js\njavascript\n// 刷新页面缓存\nlocalStorage.clear();\nwindow.location.reload();\n```\n\n### 5. 联系方式\n\n| 联系方式 | 具体内容 | 服务时间 |\n|---------|---------|---------|\n| 客服电话 | 400-xxx-xxxx | 9:00-18:00 |\n| 邮箱 | support@xxx.com | 24小时 |\n| QQ群 | 888888888 | 24小时 |\n\n### 6. 友情链接\n\n[用户指南](https://example.com/guide)\n[常见问题](https://example.com/faq)\n[安全须知](https://example.com/safety)\n\n---\n\n~~旧版本功能已于2024年1月1日停用~~\n\n> 感谢您的支持与理解！\n> \n> 祝您购物愉快！\n\n*系统管理团队*\n**2024年1月1日**');

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items`  (
  `CartItemID` int(0) NOT NULL AUTO_INCREMENT,
  `CartID` int(0) NOT NULL,
  `ItemID` int(0) NOT NULL,
  `Quantity` int(0) NOT NULL DEFAULT 1,
  `AddedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`CartItemID`) USING BTREE,
  INDEX `CartID`(`CartID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`CartID`) REFERENCES `shopping_cart` (`CartID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_items
-- ----------------------------
INSERT INTO `cart_items` VALUES (1, 1, 61, 2, '2025-04-11 11:03:46');
INSERT INTO `cart_items` VALUES (15, 2, 16, 1, '2025-04-13 20:24:05');
INSERT INTO `cart_items` VALUES (16, 2, 27, 1, '2025-04-13 20:24:28');

-- ----------------------------
-- Table structure for item_images
-- ----------------------------
DROP TABLE IF EXISTS `item_images`;
CREATE TABLE `item_images`  (
  `ImageID` int(0) NOT NULL AUTO_INCREMENT,
  `ItemID` int(0) NULL DEFAULT NULL,
  `ImageURL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`ImageID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  CONSTRAINT `item_images_ibfk_1` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item_images
-- ----------------------------
INSERT INTO `item_images` VALUES (3, 16, 'http://localhost:5173/uploads/64a9c19e-b176-413f-bf0e-ab132dc43645-F586941297586DAC1AA0211B3CFD7FA2.png');
INSERT INTO `item_images` VALUES (4, 16, 'http://localhost:5173/uploads/d003fa77-919a-4d77-8627-bf0674d3aa1d-FFC3D691840ECEA7C0CC3F93625068D3.png');
INSERT INTO `item_images` VALUES (6, 18, 'http://localhost:5173/uploads/a195fa00-acd6-4163-875e-9d3409fb82b0-6BCE00CD937CEECE8F726ABCE095D4F1.png');
INSERT INTO `item_images` VALUES (8, 18, 'http://localhost:5173/uploads/79512314-66e5-458a-b2d4-7b693cc5a5be-FFC3D691840ECEA7C0CC3F93625068D3.png');
INSERT INTO `item_images` VALUES (13, 18, 'http://localhost:5173/uploads/3053f428-c9a0-416d-8593-56fd71378c13-F586941297586DAC1AA0211B3CFD7FA2.png');
INSERT INTO `item_images` VALUES (14, 16, 'http://localhost:5173/uploads/8ffe157e-78af-42d4-aef7-2fac4f6e8131-6BCE00CD937CEECE8F726ABCE095D4F1.png');
INSERT INTO `item_images` VALUES (21, 21, 'http://localhost:5173/uploads/64edb5dd-ee96-485d-9b12-a1f5e78eaabd-O1CN01VeF4yX1umgyYGfB1m_!!2213274296080-0-alimamacc.jpg_468x468q75.jpg_.jpg');
INSERT INTO `item_images` VALUES (24, 25, 'http://localhost:5173/uploads/fd180f42-e9cc-4605-aecf-7ec01c8201b4-image_editor_1702124953957.jpg');
INSERT INTO `item_images` VALUES (25, 26, 'http://localhost:5173/uploads/4f6cfd5a-db57-49f8-83f0-32a7d21c96ff-image_editor_1702125207839.jpg');
INSERT INTO `item_images` VALUES (27, 27, 'http://localhost:5173/uploads/8e5a91ca-76b5-4c1a-b803-f0ecf8115e9a-image_editor_1702125339231.jpg');
INSERT INTO `item_images` VALUES (28, 28, 'http://localhost:5173/uploads/38b48efa-dc90-44dc-a988-700fa0e3fea9-image_editor_1702125795552.jpg');
INSERT INTO `item_images` VALUES (29, 29, 'http://localhost:5173/uploads/944f6a58-2bd4-497e-b196-e2887e12d8f0-OIP-C.jpg');
INSERT INTO `item_images` VALUES (30, 30, 'http://localhost:5173/uploads/bda271d0-9fb7-41c6-87e3-a9a1e4c6feb7-image_editor_1702126177990.jpg');
INSERT INTO `item_images` VALUES (31, 31, 'http://localhost:5173/uploads/433e82c8-0d24-43d1-99f4-c615ea37020c-image_editor_1702126341998.jpg');
INSERT INTO `item_images` VALUES (32, 32, 'http://localhost:5173/uploads/de34c222-8c24-46ff-b02c-1fe1ec00a56f-image_editor_1702126478921.jpg');
INSERT INTO `item_images` VALUES (34, 33, 'http://localhost:5173/uploads/5a3570a2-a161-4d78-9910-d14e71e9aed3-image_editor_1702126653795.jpg');
INSERT INTO `item_images` VALUES (35, 34, 'http://localhost:5173/uploads/bcd0d9b0-8739-407d-a8a4-d970a3b4b970-image_editor_1702126827024.jpg');
INSERT INTO `item_images` VALUES (36, 35, 'http://localhost:5173/uploads/d345989c-aa88-431a-9ede-94d8b24752c1-image_editor_1702126899647.jpg');
INSERT INTO `item_images` VALUES (37, 36, 'http://localhost:5173/uploads/19ca5574-e5a6-4466-a7f6-323b639f5a56-image_editor_1702127047673.jpg');
INSERT INTO `item_images` VALUES (38, 37, 'http://localhost:5173/uploads/ef40b6f9-e0a5-46af-9505-908b1b6029e8-image_editor_1702127147940.jpg');
INSERT INTO `item_images` VALUES (39, 38, 'http://localhost:5173/uploads/d44c35a2-226f-4747-906f-bea924c24452-image_editor_1702127235855.jpg');
INSERT INTO `item_images` VALUES (40, 40, 'http://localhost:5173/uploads/e1928a66-182f-49cb-bc63-ce7b24b7d0f5-image_editor_1702140072242.jpg');
INSERT INTO `item_images` VALUES (41, 41, 'http://localhost:5173/uploads/e9e16f61-9c82-4966-8afb-86c018f9438e-image_editor_1702140120998.jpg');
INSERT INTO `item_images` VALUES (42, 43, 'http://localhost:5173/uploads/29fb4e9e-134f-4fa3-9fab-ece5f6595dc0-nature-3787200_1280.jpg');
INSERT INTO `item_images` VALUES (43, 44, 'http://localhost:5173/uploads/1b7ffae1-f960-4d58-8ec1-7d94cd57c528-river-8420934_1280.jpg');
INSERT INTO `item_images` VALUES (44, 57, 'http://localhost:5173/uploads/a64e9fb1-703a-44e9-9189-577ab73b79fa-dog-8272860_1280.jpg');

-- ----------------------------
-- Table structure for items
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items`  (
  `ItemID` int(0) NOT NULL AUTO_INCREMENT,
  `SellerID` int(0) NOT NULL,
  `Name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Price` decimal(10, 2) NOT NULL,
  `Category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Quantity` int(0) NULL DEFAULT 1,
  `Status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `UpdatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`ItemID`) USING BTREE,
  INDEX `SellerID`(`SellerID`) USING BTREE,
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`SellerID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items` VALUES (16, 1, '罗技机械键盘', '女大自用99新，先到先得！', 249.00, '电子数码', '99新', 6, '已上架', '2023-12-05 15:09:22', '2025-04-13 20:50:40');
INSERT INTO `items` VALUES (18, 5, '迈从X75机械键盘', '自用99新，极光冰淇淋轴，已经润过了', 348.00, '电子数码', '99新', 9, '已上架', '2023-12-06 07:44:16', '2025-04-13 20:49:15');
INSERT INTO `items` VALUES (20, 1, '笔记本', '毕业了用不来，便宜出了，总共两本，一起出', 20.00, '家居用品', '全新', 10, '已上架', '2023-12-07 13:25:01', '2025-04-11 12:57:44');
INSERT INTO `items` VALUES (21, 1, '海澜之家睡衣', '亲戚送的，没穿过', 98.00, '服饰鞋帽', '全新', 10, '已上架', '2023-12-07 13:45:09', '2025-04-11 12:57:46');
INSERT INTO `items` VALUES (25, 1, '游戏棋', '8成新 打开后玩过2次就放柜子里了 本品不包邮，建议自提，路过自取最合适', 20.00, '儿童玩具', '8成新', 10, '已上架', '2023-12-09 12:32:37', '2025-04-11 12:57:47');
INSERT INTO `items` VALUES (26, 1, '电动车', '小牛U+B动力版6999 米其林轮胎一对720 后碟刹 980前面减震1200尾架加坐垫465 脚撑198脚踏145后面火箭筒 180中箱499 带电池48v26a有app可过户钥匙', 6000.00, '汽车用品', '95新', 1, '已上架', '2023-12-09 12:34:38', '2025-04-11 11:19:27');
INSERT INTO `items` VALUES (27, 1, 'Dickies靴子', 'Dickies马丁靴男2022夏季薄款英伦风真皮男士休闲工装靴高帮鞋子，全新，家里鞋子太多了，原价439买的，288包邮出，不退不换', 288.00, '服饰鞋帽', '全新', 10, '已上架', '2023-12-09 12:36:54', '2025-04-11 12:57:49');
INSERT INTO `items` VALUES (28, 1, '卧室台灯', '轻微使用痕迹', 30.00, '家居用品', '95新', 1, '已上架', '2023-12-09 12:43:56', '2025-04-11 11:19:30');
INSERT INTO `items` VALUES (29, 1, '爱因斯坦的智商', '只需0.01，智商送到家', 0.01, '医疗保健', '全新', 1, '已上架', '2023-12-09 12:45:29', '2025-04-11 11:19:31');
INSERT INTO `items` VALUES (30, 1, '图书', '全套4册19包邮 儿童立体书3d翻翻书1-2岁3岁宝宝绘本一岁半两岁故事书籍早教书本婴幼儿益智启蒙认知硬壳0到3岁机关洞洞书藏起来的小秘密', 19.00, '图书音像', '全新', 1, '已上架', '2023-12-09 12:50:35', '2025-04-11 03:33:14');
INSERT INTO `items` VALUES (31, 1, '《球面商业规律》书', '书籍，书，图书 球面商业规律 商业理论类书    商业理论书籍 《球面商业规律》谢显峰-著 书本 全新未拆封', 20.00, '图书音像', '全新', 1, '已上架', '2023-12-09 12:53:39', '2025-04-11 03:33:30');
INSERT INTO `items` VALUES (32, 1, '漫画书', '书本就看过一遍，里面很新 感兴趣的话点“我想要”和我私聊吧～', 13.00, '图书音像', '95新', 1, '已上架', '2023-12-09 12:55:24', '2025-04-11 03:38:16');
INSERT INTO `items` VALUES (33, 1, '地勤冬皮靴', '地勤冬皮靴羊毛内里不挑 感兴趣的话点“我想要”和我私聊吧～', 60.00, '服饰鞋帽', '9成新', 1, '已上架', '2023-12-09 12:58:28', '2025-04-11 11:19:33');
INSERT INTO `items` VALUES (34, 1, '资生堂小样', '资生堂护肤品小样套装，全新未使用，一口价60元，包邮！ 感兴趣的话点“我想要”和我私聊吧～', 60.00, '美妆护肤', '全新', 1, '已上架', '2023-12-09 13:01:12', '2025-04-11 11:19:34');
INSERT INTO `items` VALUES (35, 1, '珀莱雅双抗水乳套装', '全新  全新！珀莱雅双抗水乳套装抗初老补水保湿化妆护肤套盒 珀莱雅弹润透亮青春洁面乳60g 珀莱雅弹润透亮青春活力水60ml 珀莱雅额弹润透亮青春活力乳60ml 随机送面膜两片 99包邮偏远地区不包邮 拍下不退不换出给有需要的姐妹', 99.00, '美妆护肤', '全新', 1, '已上架', '2023-12-09 13:02:35', '2025-04-11 11:19:37');
INSERT INTO `items` VALUES (36, 1, '欧莱雅金致臻颜花蜜精华蜜保湿补水', '欧莱雅金致臻颜花蜜精华蜜保湿补水滋润爽肤水柔肤水面部护肤品 欧莱雅精华蜜130ml 全新 正品 拆封未使用 日期到2026年 可私聊发购买记录', 100.00, '美妆护肤', '全新', 1, '已上架', '2023-12-09 13:05:17', '2025-04-11 03:38:35');
INSERT INTO `items` VALUES (37, 1, '欧美创意工艺品', '欧美创意工艺品美女白马摆件客厅玄关家居装饰品陶瓷摆设结婚礼物 感兴趣的话点“我想要”和我私聊吧～', 30.00, '工艺礼品', '95新', 1, '已上架', '2023-12-09 13:07:00', '2025-04-11 03:38:41');
INSERT INTO `items` VALUES (38, 1, '琉璃工艺品', '琉璃工艺品定制 小摆件 生日礼物 圣诞节礼品 偏远地区不包邮哦 感兴趣的话点“我想要”和我私聊吧', 30.00, '工艺礼品', '9成新', 1, '已上架', '2023-12-09 13:08:03', '2025-04-11 03:33:49');
INSERT INTO `items` VALUES (40, 1, '华硕主板', '华硕z370m-plus II，支持8代9代CPU，集成dvi，DP和Type-C接口，两个m2SATA接口，要的联系。 无拆无修无暗病，包测包好，挡板齐全，有23个。', 230.00, '电子数码', '95新', 1, '已上架', '2023-12-09 16:44:09', '2025-04-11 03:38:28');
INSERT INTO `items` VALUES (41, 1, '安踏套装', '孩子马上毕业了，学校要求买的，全新，195尺码。一次未穿，包装袋子都在。孩子192偏胖试穿偏小。340元购入。可私信看购入记录。', 200.00, '服饰鞋帽', '9成新', 2, '已上架', '2023-12-09 16:44:53', '2025-04-11 03:38:46');
INSERT INTO `items` VALUES (43, 5, '测试订单2', '111', 111.00, '服饰鞋帽', '99新', 1, '已上架', '2023-12-10 06:56:28', '2025-04-11 11:42:04');
INSERT INTO `items` VALUES (44, 5, '测试订单3', '111', 111.00, '服饰鞋帽', '全新', 1, '已上架', '2023-12-10 06:56:37', '2025-04-11 11:19:46');
INSERT INTO `items` VALUES (57, 5, '123', '123', 123.00, '家居用品', '99新', 1, '已上架', '2023-12-25 08:14:17', '2025-04-11 11:19:44');
INSERT INTO `items` VALUES (58, 1, 'iphone13pro', '自用三年', 1999.00, '电子数码', '8成新', 8, '已上架', '2024-11-21 07:11:37', '2025-04-13 17:01:08');
INSERT INTO `items` VALUES (59, 29, '炉石传说一把爽局', '超爽对局', 0.99, '电子数码', '全新', 1, '已上架', '2024-11-22 03:05:28', '2025-04-11 11:41:52');
INSERT INTO `items` VALUES (60, 30, '二手纸巾', '没用过', 12.00, '家居用品', '全新', 1, '已上架', '2024-11-25 02:56:46', '2025-04-11 11:41:54');
INSERT INTO `items` VALUES (61, 30, '办公室水杯', '全新的，买来没用过，仲裁表发财了，不需要上班了', 19.00, '家居用品', '全新', 5, '已上架', '2024-11-25 06:39:45', '2025-04-11 04:56:23');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `MessageID` int(0) NOT NULL AUTO_INCREMENT,
  `FromUserID` int(0) NOT NULL,
  `ToUserID` int(0) NOT NULL,
  `Content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `SentAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`MessageID`) USING BTREE,
  INDEX `FromUserID`(`FromUserID`) USING BTREE,
  INDEX `ToUserID`(`ToUserID`) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`FromUserID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`ToUserID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (10, 5, 1, '你是管理员吗？\n', '2023-12-16 18:54:24');
INSERT INTO `messages` VALUES (11, 1, 5, '是的我是管理员', '2023-12-16 18:55:00');
INSERT INTO `messages` VALUES (12, 1, 5, '有什么事吗？\n', '2023-12-17 16:40:22');
INSERT INTO `messages` VALUES (13, 5, 1, '没事，我就问问\n', '2023-12-17 16:40:28');
INSERT INTO `messages` VALUES (14, 1, 5, '好的亲\n', '2023-12-17 16:40:31');
INSERT INTO `messages` VALUES (15, 30, 1, '你好\n', '2023-12-25 15:39:23');
INSERT INTO `messages` VALUES (16, 30, 5, '你好\n', '2023-12-25 15:40:23');
INSERT INTO `messages` VALUES (17, 5, 30, '有什么要咨询的嘛？\n', '2023-12-25 15:40:39');
INSERT INTO `messages` VALUES (58, 1, 30, '你好\n', '2024-11-25 10:34:01');
INSERT INTO `messages` VALUES (59, 1, 30, '你好\n\n', '2024-11-25 10:59:30');
INSERT INTO `messages` VALUES (60, 30, 1, 'hello', '2024-11-25 12:29:42');
INSERT INTO `messages` VALUES (61, 1, 5, '你好', '2024-12-30 11:28:53');
INSERT INTO `messages` VALUES (62, 1, 29, '你好', '2024-12-30 11:29:00');
INSERT INTO `messages` VALUES (63, 5, 30, '你好', '2025-04-01 18:07:48');

-- ----------------------------
-- Table structure for order_details
-- ----------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details`  (
  `OrderDetailID` int(0) NOT NULL AUTO_INCREMENT,
  `OrderID` int(10) UNSIGNED ZEROFILL NOT NULL,
  `ItemID` int(0) NOT NULL,
  `ItemName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `ItemPrice` decimal(10, 2) NOT NULL,
  `Quantity` int(0) NOT NULL DEFAULT 1,
  `ItemImage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`OrderDetailID`) USING BTREE,
  INDEX `OrderID`(`OrderID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `orders` (`OrderID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_details
-- ----------------------------
INSERT INTO `order_details` VALUES (1, 0000000009, 21, '海澜之家睡衣', 98.00, 1, 'http://localhost:5173/uploads/64edb5dd-ee96-485d-9b12-a1f5e78eaabd-O1CN01VeF4yX1umgyYGfB1m_!!2213274296080-0-alimamacc.jpg_468x468q75.jpg_.jpg');
INSERT INTO `order_details` VALUES (2, 0000000010, 43, '测试订单2', 111.00, 1, 'http://localhost:5173/uploads/29fb4e9e-134f-4fa3-9fab-ece5f6595dc0-nature-3787200_1280.jpg');
INSERT INTO `order_details` VALUES (3, 0000000011, 59, '炉石传说一把爽局', 0.99, 1, NULL);
INSERT INTO `order_details` VALUES (4, 0000000013, 60, '二手纸巾', 12.00, 1, NULL);
INSERT INTO `order_details` VALUES (5, 0000000016, 16, '罗技机械键盘', 249.00, 1, 'http://localhost:5173/uploads/64a9c19e-b176-413f-bf0e-ab132dc43645-F586941297586DAC1AA0211B3CFD7FA2.png');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `OrderID` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `BuyerID` int(0) NOT NULL,
  `ItemID` int(0) NOT NULL,
  `Price` decimal(10, 2) NOT NULL,
  `OrderStatus` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Pending',
  `CreatedAt` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `CompletedAt` datetime(0) NOT NULL,
  `SellerID` int(0) NOT NULL,
  `ItemName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `RecipientName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `PhoneNumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `Quantity` int(0) NOT NULL,
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `BuyerID`(`BuyerID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  INDEX `SellerID`(`SellerID`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`BuyerID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`SellerID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (0000000009, 5, 21, 98.00, '已完成', '2025-01-13 12:00:27', '2025-01-14 00:49:36', 1, '海澜之家睡衣', 'cjm', '地球村', '18115175317', NULL, 1);
INSERT INTO `orders` VALUES (0000000010, 1, 43, 111.00, '已完成', '2025-01-15 10:35:02', '2025-01-15 18:35:24', 5, '测试订单2', 'admin', '南京工业大学浦江学院', '18862771302', NULL, 1);
INSERT INTO `orders` VALUES (0000000011, 1, 59, 0.99, '已完成', '2025-01-18 10:01:48', '2025-01-18 11:26:20', 29, '炉石传说一把爽局', 'admin', '南京工业大学浦江学院', '18862771302', NULL, 1);
INSERT INTO `orders` VALUES (0000000013, 1, 60, 12.00, '已完成', '2025-01-19 04:31:25', '2025-01-19 12:32:30', 30, '二手纸巾', 'admin', '南京工业大学浦江学院', '18862771302', NULL, 1);
INSERT INTO `orders` VALUES (0000000016, 30, 16, 249.00, '已取消', '2025-04-11 05:25:43', '2025-04-13 15:48:29', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '放驿站', 1);
INSERT INTO `orders` VALUES (0000000017, 30, 16, 249.00, '已取消', '2025-04-13 07:18:41', '2025-04-13 15:43:16', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '', 1);
INSERT INTO `orders` VALUES (0000000018, 30, 16, 249.00, '申请退款中', '2025-04-13 07:56:16', '2025-04-13 15:56:48', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '', 1);
INSERT INTO `orders` VALUES (0000000019, 30, 58, 4247.00, '已取消', '2025-04-13 08:59:44', '2025-04-13 17:01:08', 1, 'iphone13pro 等 2 件商品', 'test', '南京浦江', '18273672893', '', 2);
INSERT INTO `orders` VALUES (0000000020, 30, 16, 289.00, '已取消', '2025-04-13 09:19:34', '2025-04-13 17:23:58', 1, '罗技机械键盘 等多件商品', 'test', '南京浦江', '18273672893', '', 3);
INSERT INTO `orders` VALUES (0000000021, 30, 16, 269.00, '待支付', '2025-04-13 09:24:57', '2025-04-13 09:24:57', 1, '罗技机械键盘 等多件商品', 'test', '南京浦江', '18273672893', '', 2);
INSERT INTO `orders` VALUES (0000000022, 30, 16, 269.00, '待支付', '2025-04-13 09:29:04', '2025-04-13 09:29:04', 1, '罗技机械键盘 等多件商品', 'test', '南京浦江', '18273672893', '', 2);
INSERT INTO `orders` VALUES (0000000023, 30, 16, 249.00, '待支付', '2025-04-13 09:35:41', '2025-04-13 09:35:41', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '', 1);
INSERT INTO `orders` VALUES (0000000024, 30, 16, 249.00, '已取消', '2025-04-13 09:39:36', '2025-04-13 18:05:19', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '', 1);
INSERT INTO `orders` VALUES (0000000025, 30, 18, 348.00, '已支付', '2025-04-13 09:39:36', '2025-04-13 20:49:15', 5, '迈从X75机械键盘', 'test', '南京浦江', '18273672893', '', 1);
INSERT INTO `orders` VALUES (0000000026, 30, 16, 249.00, '已发货', '2025-04-13 12:50:35', '2025-04-13 20:59:16', 1, '罗技机械键盘', 'test', '南京浦江', '18273672893', '', 1);

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `CartID` int(0) NOT NULL AUTO_INCREMENT,
  `UserID` int(0) NOT NULL,
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `UpdatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`CartID`) USING BTREE,
  INDEX `UserID`(`UserID`) USING BTREE,
  CONSTRAINT `shopping_cart_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `users` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------
INSERT INTO `shopping_cart` VALUES (1, 1, '2025-04-11 11:03:46', '2025-04-11 11:03:46');
INSERT INTO `shopping_cart` VALUES (2, 30, '2025-04-11 12:55:58', '2025-04-11 12:55:58');

-- ----------------------------
-- Table structure for userfavorites
-- ----------------------------
DROP TABLE IF EXISTS `userfavorites`;
CREATE TABLE `userfavorites`  (
  `FavoriteID` int(0) NOT NULL AUTO_INCREMENT,
  `UserID` int(0) NOT NULL,
  `ItemID` int(0) NOT NULL,
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`FavoriteID`) USING BTREE,
  INDEX `UserID`(`UserID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  CONSTRAINT `userfavorites_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `userfavorites_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userfavorites
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `UserID` int(0) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `PasswordHash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `PhoneNumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `WalletBalance` decimal(10, 2) NULL DEFAULT NULL,
  `RecipientName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `UpdatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `Role` int(0) NOT NULL DEFAULT 1,
  `Avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `NickName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`UserID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '1487072060@qq.com', '21232f297a57a5a743894a0e4a801fc3', '18862771302', 2287.25, 'admin', '南京工业大学浦江学院', '2023-11-17 08:30:00', '2025-04-13 20:50:40', 0, 'http://localhost:5173/uploads/30ae16ea-05b2-44da-92d0-b33c01cab496-100.jpg', '管理员');
INSERT INTO `users` VALUES (5, '1678147275', 'gengvhen@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', '18115175317', 557.60, 'cjm', '地球村', '2023-11-18 07:14:11', '2025-04-13 20:49:15', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', 'charlotte');
INSERT INTO `users` VALUES (29, '1487072060', '14834324060@qq.com', 'e10adc3949ba59abbe56e057f20f883e', '13845624658', 0.99, '张某某', '江苏省', '2023-12-16 10:28:51', '2024-11-22 18:01:48', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', '张某');
INSERT INTO `users` VALUES (30, '123456', '123123@qq.com', 'e10adc3949ba59abbe56e057f20f883e', '18273672893', 4609.88, 'test', '南京浦江', '2023-12-25 07:33:34', '2025-04-13 17:23:58', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', '123456');

SET FOREIGN_KEY_CHECKS = 1;
