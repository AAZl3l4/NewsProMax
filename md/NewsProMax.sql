/*
 Navicat Premium Data Transfer

 Source Server         : Ubuntu环境机
 Source Server Type    : MySQL
 Source Server Version : 80406
 Source Host           : 192.168.188.188:3306
 Source Schema         : NewsProMax

 Target Server Type    : MySQL
 Target Server Version : 80406
 File Encoding         : 65001

 Date: 05/08/2025 23:38:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '0:不默认 1:默认',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `creation_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userid` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `news_id` int NOT NULL,
  `like_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of like
-- ----------------------------

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `msg_type` tinyint NOT NULL COMMENT '1单聊 2群聊',
  `from_uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `to_uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单聊时对方uid',
  `group_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群聊时群id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `msg_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `del_flag` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1951206535813214209 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for news_category
-- ----------------------------
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news_category
-- ----------------------------

-- ----------------------------
-- Table structure for news_content
-- ----------------------------
DROP TABLE IF EXISTS `news_content`;
CREATE TABLE `news_content`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  `news_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news_content
-- ----------------------------

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` int NOT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '0:下单 1:支付 2:发货 3:收货 4:退货',
  `amount` double NOT NULL,
  `user_id` int NOT NULL,
  `user_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `user_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `unit_price` double NOT NULL,
  `quantity` int NOT NULL,
  `amount` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1001, 1, '苹果耳机', 'http://img.com/apple-earphone.jpg', 99, 2, 198);
INSERT INTO `order_item` VALUES (2, 1001, 2, '华为手环', 'http://img.com/huawei-band.jpg', 199, 1, 199);
INSERT INTO `order_item` VALUES (3, 1002, 3, '小米电视 55寸', 'http://img.com/mi-tv55.jpg', 2499, 1, 2499);
INSERT INTO `order_item` VALUES (4, 1003, 1, '苹果耳机', 'http://img.com/apple-earphone.jpg', 99, 1, 99);
INSERT INTO `order_item` VALUES (5, 1004, 4, '罗技鼠标', 'http://img.com/logitech-mouse.jpg', 129, 3, 387);
INSERT INTO `order_item` VALUES (6, 1005, 5, '樱桃机械键盘', 'http://img.com/cherry-keyboard.jpg', 499, 1, 499);
INSERT INTO `order_item` VALUES (7, 1005, 2, '华为手环', 'http://img.com/huawei-band.jpg', 199, 2, 398);
INSERT INTO `order_item` VALUES (8, 1006, 6, '戴森吹风机', 'http://img.com/dyson-hair.jpg', 2999, 1, 2999);
INSERT INTO `order_item` VALUES (9, 1007, 7, 'Switch 游戏机', 'http://img.com/switch.jpg', 2099, 1, 2099);
INSERT INTO `order_item` VALUES (10, 1008, 8, '索尼 WH-1000XM5', 'http://img.com/sony-wh1000.jpg', 2299, 1, 2299);
INSERT INTO `order_item` VALUES (11, 1009, 9, 'iPad Pro 11寸', 'http://img.com/ipad-pro.jpg', 6199, 1, 6199);
INSERT INTO `order_item` VALUES (12, 1010, 10, 'MacBook Air M2', 'http://img.com/mba-m2.jpg', 9499, 1, 9499);
INSERT INTO `order_item` VALUES (13, 1011, 1, '苹果耳机', 'http://img.com/apple-earphone.jpg', 99, 5, 495);
INSERT INTO `order_item` VALUES (14, 1012, 11, 'AirPods Pro 2', 'http://img.com/airpods-pro.jpg', 1899, 2, 3798);
INSERT INTO `order_item` VALUES (15, 1013, 12, '米家扫拖机器人', 'http://img.com/mi-robot.jpg', 1699, 1, 1699);
INSERT INTO `order_item` VALUES (16, 1014, 13, '格力空调 1.5匹', 'http://img.com/gree-ac.jpg', 2799, 1, 2799);
INSERT INTO `order_item` VALUES (17, 1015, 14, '美的电饭煲', 'http://img.com/midea-cooker.jpg', 399, 1, 399);
INSERT INTO `order_item` VALUES (18, 1016, 15, 'OPPO 充电器 65W', 'http://img.com/oppo-charger.jpg', 89, 3, 267);
INSERT INTO `order_item` VALUES (19, 1017, 16, 'vivo 数据线', 'http://img.com/vivo-cable.jpg', 29, 10, 290);
INSERT INTO `order_item` VALUES (20, 1018, 17, '雷蛇游戏耳机', 'http://img.com/razer-headset.jpg', 799, 1, 799);

-- ----------------------------
-- Table structure for role_review
-- ----------------------------
DROP TABLE IF EXISTS `role_review`;
CREATE TABLE `role_review`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL COMMENT '申请用户的id',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户申请的角色',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '审核的状态 0：未审核 1审核通过 3审核不通过',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updata_time` datetime NULL DEFAULT NULL COMMENT '审核更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_review
-- ----------------------------

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid` ASC, `branch_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `age` int UNSIGNED NOT NULL,
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '男 女',
  `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT 'ADMIN 管理员 USER用户 MERCHANT商家 UPUP主',
  `isban` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '0:正常 1:禁止',
  `create_time` datetime NULL DEFAULT NULL,
  `money` double NULL DEFAULT 0,
  `wx_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `wx_id`(`wx_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', 'http://192.168.188.188:9000/public/default.png', 'admin@qq.com', 20, '男', 'ADMIN', '0', '2025-07-31 14:58:56', 0, 'oy_OnvuTGzeLysFCUJbGa7jyaJzs');
INSERT INTO `user` VALUES (2, 'user', '123456', 'http://192.168.188.188:9000/public/default.png', 'user@qq.com', 20, '男', 'USER', '0', '2025-07-30 16:56:15', 0, '');

SET FOREIGN_KEY_CHECKS = 1;
