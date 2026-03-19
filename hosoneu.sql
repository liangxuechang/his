/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80408
 Source Host           : 127.0.0.1:3306
 Source Schema         : hosoneu

 Target Server Type    : MySQL
 Target Server Version : 80408
 File Encoding         : 65001

 Date: 19/03/2026 22:45:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `Role_ID` int(0) NOT NULL AUTO_INCREMENT,
  `Function_id` int(0) NULL DEFAULT NULL,
  `Role_Name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`Role_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 1, '普通用户');
INSERT INTO `role` VALUES (2, 2, '管理员');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `User_ID` int(0) NOT NULL AUTO_INCREMENT,
  `User_loginName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `User_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Role_ID` int(0) NULL DEFAULT NULL,
  `User_Name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Department_ID` int(0) NULL DEFAULT NULL,
  `User_Title_ID` int(0) NULL DEFAULT NULL,
  `User_Gender` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `User_Status` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `User_Scheduling_LimitCount` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`User_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'xiaoming', '$2a$12$FOfsgsqozCzXiAy7MObLAeYyQ/bfsLCb3Jfks38i.ufF5hzUZTQYi', 1, '小明', 1, 1, '1', '1', 1);
INSERT INTO `user` VALUES (2, 'xiaohong', '$2a$12$FOfsgsqozCzXiAy7MObLAeYyQ/bfsLCb3Jfks38i.ufF5hzUZTQYi', 2, '小红', 1, 1, '1', '1', 1);
INSERT INTO `user` VALUES (3, 'xiaoliang', '$2a$12$FOfsgsqozCzXiAy7MObLAeYyQ/bfsLCb3Jfks38i.ufF5hzUZTQYi', 1, '小亮', 1, 1, '1', '1', 1);

SET FOREIGN_KEY_CHECKS = 1;
