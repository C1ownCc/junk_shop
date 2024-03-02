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

 Date: 28/12/2023 23:09:40
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '***此版本仅用于测试！！！！你好***');

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
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item_images
-- ----------------------------
INSERT INTO `item_images` VALUES (3, 16, 'http://localhost:8080/uploads/64a9c19e-b176-413f-bf0e-ab132dc43645-F586941297586DAC1AA0211B3CFD7FA2.png');
INSERT INTO `item_images` VALUES (4, 16, 'http://localhost:8080/uploads/d003fa77-919a-4d77-8627-bf0674d3aa1d-FFC3D691840ECEA7C0CC3F93625068D3.png');
INSERT INTO `item_images` VALUES (6, 18, 'http://localhost:8080/uploads/a195fa00-acd6-4163-875e-9d3409fb82b0-6BCE00CD937CEECE8F726ABCE095D4F1.png');
INSERT INTO `item_images` VALUES (8, 18, 'http://localhost:8080/uploads/79512314-66e5-458a-b2d4-7b693cc5a5be-FFC3D691840ECEA7C0CC3F93625068D3.png');
INSERT INTO `item_images` VALUES (13, 18, 'http://localhost:8080/uploads/3053f428-c9a0-416d-8593-56fd71378c13-F586941297586DAC1AA0211B3CFD7FA2.png');
INSERT INTO `item_images` VALUES (14, 16, 'http://localhost:8080/uploads/8ffe157e-78af-42d4-aef7-2fac4f6e8131-6BCE00CD937CEECE8F726ABCE095D4F1.png');
INSERT INTO `item_images` VALUES (20, 20, 'http://localhost:8080/uploads/453b56d6-680d-49e6-8bcc-d4ae883e4515-pexels-karolina-grabowska-4464481.jpg');
INSERT INTO `item_images` VALUES (21, 21, 'http://localhost:8080/uploads/64edb5dd-ee96-485d-9b12-a1f5e78eaabd-O1CN01VeF4yX1umgyYGfB1m_!!2213274296080-0-alimamacc.jpg_468x468q75.jpg_.jpg');
INSERT INTO `item_images` VALUES (24, 25, 'http://localhost:8080/uploads/fd180f42-e9cc-4605-aecf-7ec01c8201b4-image_editor_1702124953957.jpg');
INSERT INTO `item_images` VALUES (25, 26, 'http://localhost:8080/uploads/4f6cfd5a-db57-49f8-83f0-32a7d21c96ff-image_editor_1702125207839.jpg');
INSERT INTO `item_images` VALUES (27, 27, 'http://localhost:8080/uploads/8e5a91ca-76b5-4c1a-b803-f0ecf8115e9a-image_editor_1702125339231.jpg');
INSERT INTO `item_images` VALUES (28, 28, 'http://localhost:8080/uploads/38b48efa-dc90-44dc-a988-700fa0e3fea9-image_editor_1702125795552.jpg');
INSERT INTO `item_images` VALUES (29, 29, 'http://localhost:8080/uploads/944f6a58-2bd4-497e-b196-e2887e12d8f0-OIP-C.jpg');
INSERT INTO `item_images` VALUES (30, 30, 'http://localhost:8080/uploads/bda271d0-9fb7-41c6-87e3-a9a1e4c6feb7-image_editor_1702126177990.jpg');
INSERT INTO `item_images` VALUES (31, 31, 'http://localhost:8080/uploads/433e82c8-0d24-43d1-99f4-c615ea37020c-image_editor_1702126341998.jpg');
INSERT INTO `item_images` VALUES (32, 32, 'http://localhost:8080/uploads/de34c222-8c24-46ff-b02c-1fe1ec00a56f-image_editor_1702126478921.jpg');
INSERT INTO `item_images` VALUES (34, 33, 'http://localhost:8080/uploads/5a3570a2-a161-4d78-9910-d14e71e9aed3-image_editor_1702126653795.jpg');
INSERT INTO `item_images` VALUES (35, 34, 'http://localhost:8080/uploads/bcd0d9b0-8739-407d-a8a4-d970a3b4b970-image_editor_1702126827024.jpg');
INSERT INTO `item_images` VALUES (36, 35, 'http://localhost:8080/uploads/d345989c-aa88-431a-9ede-94d8b24752c1-image_editor_1702126899647.jpg');
INSERT INTO `item_images` VALUES (37, 36, 'http://localhost:8080/uploads/19ca5574-e5a6-4466-a7f6-323b639f5a56-image_editor_1702127047673.jpg');
INSERT INTO `item_images` VALUES (38, 37, 'http://localhost:8080/uploads/ef40b6f9-e0a5-46af-9505-908b1b6029e8-image_editor_1702127147940.jpg');
INSERT INTO `item_images` VALUES (39, 38, 'http://localhost:8080/uploads/d44c35a2-226f-4747-906f-bea924c24452-image_editor_1702127235855.jpg');
INSERT INTO `item_images` VALUES (40, 40, 'http://localhost:8080/uploads/e1928a66-182f-49cb-bc63-ce7b24b7d0f5-image_editor_1702140072242.jpg');
INSERT INTO `item_images` VALUES (41, 41, 'http://localhost:8080/uploads/e9e16f61-9c82-4966-8afb-86c018f9438e-image_editor_1702140120998.jpg');
INSERT INTO `item_images` VALUES (42, 43, 'http://localhost:8080/uploads/29fb4e9e-134f-4fa3-9fab-ece5f6595dc0-nature-3787200_1280.jpg');
INSERT INTO `item_images` VALUES (43, 44, 'http://localhost:8080/uploads/1b7ffae1-f960-4d58-8ec1-7d94cd57c528-river-8420934_1280.jpg');
INSERT INTO `item_images` VALUES (44, 57, 'http://localhost:8080/uploads/a64e9fb1-703a-44e9-9189-577ab73b79fa-dog-8272860_1280.jpg');

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
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items` VALUES (16, 1, '罗技机械键盘', '女大自用99新，先到先得！', 249.00, '电子数码', '99新', 1, '已上架', '2023-12-05 15:09:22', '2023-12-07 12:56:20');
INSERT INTO `items` VALUES (18, 5, '迈从X75机械键盘', '自用99新，极光冰淇淋轴，已经润过了', 348.00, '电子数码', '99新', 1, '已上架', '2023-12-06 07:44:16', '2023-12-19 07:31:45');
INSERT INTO `items` VALUES (20, 1, '笔记本', '毕业了用不来，便宜出了，总共两本，一起出', 20.00, '家居用品', '全新', 0, '已上架', '2023-12-07 13:25:01', '2023-12-07 13:46:04');
INSERT INTO `items` VALUES (21, 1, '海澜之家睡衣', '亲戚送的，没穿过', 98.00, '服饰鞋帽', '全新', 0, '已售出', '2023-12-07 13:45:09', '2023-12-13 20:00:27');
INSERT INTO `items` VALUES (25, 1, '游戏棋', '8成新 打开后玩过2次就放柜子里了 本品不包邮，建议自提，路过自取最合适', 20.00, '儿童玩具', '8成新', 0, '已上架', '2023-12-09 12:32:37', '2023-12-09 12:33:09');
INSERT INTO `items` VALUES (26, 1, '电动车', '小牛U+B动力版6999 米其林轮胎一对720 后碟刹 980前面减震1200尾架加坐垫465 脚撑198脚踏145后面火箭筒 180中箱499 带电池48v26a有app可过户钥匙', 6000.00, '汽车用品', '95新', 0, '已上架', '2023-12-09 12:34:38', '2023-12-09 16:26:10');
INSERT INTO `items` VALUES (27, 1, 'Dickies靴子', 'Dickies马丁靴男2022夏季薄款英伦风真皮男士休闲工装靴高帮鞋子，全新，家里鞋子太多了，原价439买的，288包邮出，不退不换', 288.00, '服饰鞋帽', '全新', 0, '已上架', '2023-12-09 12:36:54', '2023-12-09 16:26:16');
INSERT INTO `items` VALUES (28, 1, '卧室台灯', '轻微使用痕迹', 30.00, '家居用品', '95新', 0, '已上架', '2023-12-09 12:43:56', '2023-12-09 16:26:21');
INSERT INTO `items` VALUES (29, 1, '爱因斯坦的智商', '只需0.01，智商送到家', 0.01, '医疗保健', '全新', 0, '已上架', '2023-12-09 12:45:29', '2023-12-09 16:26:25');
INSERT INTO `items` VALUES (30, 1, '图书', '全套4册19包邮 儿童立体书3d翻翻书1-2岁3岁宝宝绘本一岁半两岁故事书籍早教书本婴幼儿益智启蒙认知硬壳0到3岁机关洞洞书藏起来的小秘密', 19.00, '图书音像', '全新', 0, '已上架', '2023-12-09 12:50:35', '2023-12-09 16:26:35');
INSERT INTO `items` VALUES (31, 1, '《球面商业规律》书', '书籍，书，图书 球面商业规律 商业理论类书    商业理论书籍 《球面商业规律》谢显峰-著 书本 全新未拆封', 20.00, '图书音像', '全新', 0, '已上架', '2023-12-09 12:53:39', '2023-12-09 16:26:37');
INSERT INTO `items` VALUES (32, 1, '漫画书', '书本就看过一遍，里面很新 感兴趣的话点“我想要”和我私聊吧～', 13.00, '图书音像', '95新', 0, '已上架', '2023-12-09 12:55:24', '2023-12-09 16:26:41');
INSERT INTO `items` VALUES (33, 1, '地勤冬皮靴', '地勤冬皮靴羊毛内里不挑 感兴趣的话点“我想要”和我私聊吧～', 60.00, '服饰鞋帽', '9成新', 0, '已上架', '2023-12-09 12:58:28', '2023-12-09 16:26:45');
INSERT INTO `items` VALUES (34, 1, '资生堂小样', '资生堂护肤品小样套装，全新未使用，一口价60元，包邮！ 感兴趣的话点“我想要”和我私聊吧～', 60.00, '美妆护肤', '全新', 0, '已上架', '2023-12-09 13:01:12', '2023-12-09 16:26:48');
INSERT INTO `items` VALUES (35, 1, '珀莱雅双抗水乳套装', '全新  全新！珀莱雅双抗水乳套装抗初老补水保湿化妆护肤套盒 珀莱雅弹润透亮青春洁面乳60g 珀莱雅弹润透亮青春活力水60ml 珀莱雅额弹润透亮青春活力乳60ml 随机送面膜两片 99包邮偏远地区不包邮 拍下不退不换出给有需要的姐妹', 99.00, '美妆护肤', '全新', 0, '已上架', '2023-12-09 13:02:35', '2023-12-09 16:26:52');
INSERT INTO `items` VALUES (36, 1, '欧莱雅金致臻颜花蜜精华蜜保湿补水', '欧莱雅金致臻颜花蜜精华蜜保湿补水滋润爽肤水柔肤水面部护肤品 欧莱雅精华蜜130ml 全新 正品 拆封未使用 日期到2026年 可私聊发购买记录', 100.00, '美妆护肤', '全新', 0, '已上架', '2023-12-09 13:05:17', '2023-12-09 16:26:56');
INSERT INTO `items` VALUES (37, 1, '欧美创意工艺品', '欧美创意工艺品美女白马摆件客厅玄关家居装饰品陶瓷摆设结婚礼物 感兴趣的话点“我想要”和我私聊吧～', 30.00, '工艺礼品', '95新', 0, '已上架', '2023-12-09 13:07:00', '2023-12-09 16:27:00');
INSERT INTO `items` VALUES (38, 1, '琉璃工艺品', '琉璃工艺品定制 小摆件 生日礼物 圣诞节礼品 偏远地区不包邮哦 感兴趣的话点“我想要”和我私聊吧', 30.00, '工艺礼品', '9成新', 0, '已上架', '2023-12-09 13:08:03', '2023-12-09 16:27:03');
INSERT INTO `items` VALUES (40, 1, '华硕主板', '华硕z370m-plus II，支持8代9代CPU，集成dvi，DP和Type-C接口，两个m2SATA接口，要的联系。 无拆无修无暗病，包测包好，挡板齐全，有23个。', 230.00, '电子数码', '95新', 0, '已上架', '2023-12-09 16:44:09', '2023-12-09 16:46:14');
INSERT INTO `items` VALUES (41, 1, '安踏套装', '孩子马上毕业了，学校要求买的，全新，195尺码。一次未穿，包装袋子都在。孩子192偏胖试穿偏小。340元购入。可私信看购入记录。', 200.00, '服饰鞋帽', '9成新', 0, '已上架', '2023-12-09 16:44:53', '2023-12-09 16:46:17');
INSERT INTO `items` VALUES (43, 5, '测试订单2', '111', 111.00, '服饰鞋帽', '99新', 0, '已售出', '2023-12-10 06:56:28', '2023-12-14 18:35:01');
INSERT INTO `items` VALUES (44, 5, '测试订单3', '111', 111.00, '服饰鞋帽', '全新', 0, '已上架', '2023-12-10 06:56:37', '2023-12-14 18:37:59');
INSERT INTO `items` VALUES (55, 1, '123大23打完', ' 达瓦达瓦大大伟大阿文', 22.00, '服饰鞋帽', '全新', 0, '审核不通过', '2023-12-11 10:56:29', '2023-12-12 10:00:50');
INSERT INTO `items` VALUES (56, 1, '123123', '123123', 11.20, '家居用品', '99新', 0, '已上架', '2023-12-13 08:23:46', '2023-12-13 11:59:00');
INSERT INTO `items` VALUES (57, 5, '123', '123', 123.00, '家居用品', '99新', 0, '已上架', '2023-12-25 08:14:17', '2023-12-25 08:14:59');

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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 1, 5, '你好', '2023-12-13 12:00:27');
INSERT INTO `messages` VALUES (2, 5, 1, 'hello', '2023-12-16 16:08:41');
INSERT INTO `messages` VALUES (4, 29, 1, 'hello\n', '2023-12-16 18:29:16');
INSERT INTO `messages` VALUES (5, 1, 29, '你好\n', '2023-12-16 18:29:20');
INSERT INTO `messages` VALUES (7, 5, 1, '测试！\n', '2023-12-16 18:43:25');
INSERT INTO `messages` VALUES (8, 1, 5, 'ok\n', '2023-12-16 18:43:36');
INSERT INTO `messages` VALUES (9, 29, 1, '顶顶顶顶\n', '2023-12-16 18:50:56');
INSERT INTO `messages` VALUES (10, 5, 1, '你是管理员吗？\n', '2023-12-16 18:54:24');
INSERT INTO `messages` VALUES (11, 1, 5, '是的我是管理员', '2023-12-16 18:55:00');
INSERT INTO `messages` VALUES (12, 1, 5, '有什么事吗？\n', '2023-12-17 16:40:22');
INSERT INTO `messages` VALUES (13, 5, 1, '没事，我就问问\n', '2023-12-17 16:40:28');
INSERT INTO `messages` VALUES (14, 1, 5, '好的亲\n', '2023-12-17 16:40:31');
INSERT INTO `messages` VALUES (15, 30, 1, '你好\n', '2023-12-25 15:39:23');
INSERT INTO `messages` VALUES (16, 30, 5, '你好\n', '2023-12-25 15:40:23');
INSERT INTO `messages` VALUES (17, 5, 30, '有什么要咨询的嘛？\n', '2023-12-25 15:40:39');
INSERT INTO `messages` VALUES (18, 30, 5, '1111\n', '2023-12-25 16:18:22');
INSERT INTO `messages` VALUES (19, 5, 30, '2222\n', '2023-12-25 16:18:26');

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
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `BuyerID`(`BuyerID`) USING BTREE,
  INDEX `ItemID`(`ItemID`) USING BTREE,
  INDEX `SellerID`(`SellerID`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`BuyerID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `items` (`ItemID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`SellerID`) REFERENCES `users` (`UserID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (0000000009, 5, 21, 98.00, '已完成', '2023-12-13 12:00:27', '2023-12-15 00:49:36', 1, '海澜之家睡衣', 'cjm', '地球村', '18115175317');
INSERT INTO `orders` VALUES (0000000010, 1, 43, 111.00, '已完成', '2023-12-14 10:35:02', '2023-12-14 18:35:24', 5, '测试订单2', 'admin', '南京工业大学浦江学院', '18862771302');

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
  `WalletBalance` decimal(10, 2) NULL,
  `RecipientName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `UpdatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `Role` int(0) NOT NULL DEFAULT 1,
  `Avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `NickName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`UserID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '1487072060@qq.com', '21232f297a57a5a743894a0e4a801fc3', '18862771302', 502.88, 'admin', '南京工业大学浦江学院', '2023-11-17 08:30:00', '2023-12-17 05:33:43', 0, 'http://localhost:8080/uploads/30ae16ea-05b2-44da-92d0-b33c01cab496-100.jpg', '管理员');
INSERT INTO `users` VALUES (5, '1678147275', 'gengvhen@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', '18115175317', 213.08, 'cjm', '地球村', '2023-11-18 07:14:11', '2023-12-20 10:12:34', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', 'charlotte');
INSERT INTO `users` VALUES (29, '1487072060', '14834324060@qq.com', '21232f297a57a5a743894a0e4a801fc3', '13845624658', 0.00, '张某某', '江苏省', '2023-12-16 10:28:51', '2023-12-16 10:28:51', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', '张某');
INSERT INTO `users` VALUES (30, '123456', '123123@qq.com', 'e10adc3949ba59abbe56e057f20f883e', '18273672893', 100.00, 'test', '南京浦江', '2023-12-25 07:33:34', '2023-12-25 16:16:00', 1, 'https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100', '123456');

SET FOREIGN_KEY_CHECKS = 1;
