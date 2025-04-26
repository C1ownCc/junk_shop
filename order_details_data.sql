-- ----------------------------
-- Records of order_details
-- ----------------------------
BEGIN;
-- 订单0000000009的详情 (海澜之家睡衣)
INSERT INTO `order_details` (`OrderID`, `ItemID`, `ItemName`, `ItemPrice`, `Quantity`, `ItemImage`) 
VALUES (0000000009, 21, '海澜之家睡衣', 98.00, 1, 'http://localhost:5173/uploads/64edb5dd-ee96-485d-9b12-a1f5e78eaabd-O1CN01VeF4yX1umgyYGfB1m_!!2213274296080-0-alimamacc.jpg_468x468q75.jpg_.jpg');

-- 订单0000000010的详情 (测试订单2)
INSERT INTO `order_details` (`OrderID`, `ItemID`, `ItemName`, `ItemPrice`, `Quantity`, `ItemImage`) 
VALUES (0000000010, 43, '测试订单2', 111.00, 1, 'http://localhost:5173/uploads/29fb4e9e-134f-4fa3-9fab-ece5f6595dc0-nature-3787200_1280.jpg');

-- 订单0000000011的详情 (炉石传说一把爽局)
INSERT INTO `order_details` (`OrderID`, `ItemID`, `ItemName`, `ItemPrice`, `Quantity`, `ItemImage`) 
VALUES (0000000011, 59, '炉石传说一把爽局', 0.99, 1, NULL);

-- 订单0000000013的详情 (二手纸巾)
INSERT INTO `order_details` (`OrderID`, `ItemID`, `ItemName`, `ItemPrice`, `Quantity`, `ItemImage`) 
VALUES (0000000013, 60, '二手纸巾', 12.00, 1, NULL);

-- 订单0000000016的详情 (罗技机械键盘)
INSERT INTO `order_details` (`OrderID`, `ItemID`, `ItemName`, `ItemPrice`, `Quantity`, `ItemImage`) 
VALUES (0000000016, 16, '罗技机械键盘', 249.00, 1, 'http://localhost:5173/uploads/64a9c19e-b176-413f-bf0e-ab132dc43645-F586941297586DAC1AA0211B3CFD7FA2.png');
COMMIT; 