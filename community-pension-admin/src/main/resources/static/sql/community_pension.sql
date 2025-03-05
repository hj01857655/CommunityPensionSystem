/*
 Navicat Premium Dump SQL

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : community_pension

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 03/03/2025 15:11:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for elder
-- ----------------------------
DROP TABLE IF EXISTS `elder`;
CREATE TABLE `elder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `birthday` datetime(6) NULL DEFAULT NULL,
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `emergency_contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `health_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `medical_history` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `allergy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '老人信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder
-- ----------------------------
INSERT INTO `elder` VALUES (1, '张大爷', 'male', '1940-05-15 00:00:00.000000', '110101194005151234', '13812345678', '北京市朝阳区某小区1号楼101室', '李长娥', '13912345678', '高血压，糖尿病', '心脏病', '青霉素', '/avatar/zhangdaye.jpg', '2025-02-26 04:02:18', '2025-02-26 04:23:21', '1');
INSERT INTO `elder` VALUES (2, '王奶奶', 'female', '1938-10-20 00:00:00.000000', '110102193810205678', '13712345678', '上海市浦东新区某小区2号楼202室', '赵万达', '13612345678', '关节炎', '无', '无', '/avatar/wangnainai.jpg', '2025-02-26 04:02:18', '2025-02-26 04:23:27', '喜欢跳广场舞');

-- ----------------------------
-- Table structure for kin
-- ----------------------------
DROP TABLE IF EXISTS `kin`;
CREATE TABLE `kin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `relationship` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '与老人关系',
  `elder_id` bigint NULL DEFAULT NULL COMMENT '关联老人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `elder_id`(`elder_id` ASC) USING BTREE,
  CONSTRAINT `kin_ibfk_1` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '老人家属表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kin
-- ----------------------------
INSERT INTO `kin` VALUES (1, '李长娥', '13912345678', '北京市朝阳区某小区1号楼101室', '女儿', 1, '2025-02-26 04:02:37', '2025-02-26 04:23:41', '无');
INSERT INTO `kin` VALUES (2, '赵万达', '13612345678', '上海市浦东新区某小区2号楼202室', '儿子', 2, '2025-02-26 04:02:37', '2025-02-26 04:23:03', '无');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'elder', '老人', '2025-02-26 04:04:22', '2025-02-26 04:04:22');
INSERT INTO `role` VALUES (2, 'kin', '老人家属', '2025-02-26 04:04:22', '2025-02-26 04:04:22');
INSERT INTO `role` VALUES (3, 'staff', '社区工作人员', '2025-02-26 04:04:22', '2025-02-26 04:04:22');
INSERT INTO `role` VALUES (4, 'admin', '管理员', '2025-02-26 04:04:22', '2025-02-26 04:04:22');

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职位',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区工作人员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES (1, '陈医生', '13512345678', '北京市朝阳区某小区3号楼303室', '医疗部', '医生', '2025-02-26 04:02:58', '2025-02-26 04:02:58', '无');
INSERT INTO `staff` VALUES (2, '孙护士', '13412345678', '上海市浦东新区某小区4号楼404室', '护理部', '护士', '2025-02-26 04:02:58', '2025-02-26 04:02:58', '无');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `elder_id` bigint NULL DEFAULT NULL COMMENT '老人ID（关联elder表）',
  `kin_id` bigint NULL DEFAULT NULL COMMENT '家属ID（关联kin表）',
  `staff_id` bigint NULL DEFAULT NULL COMMENT '员工ID（关联staff表）',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID（关联role表）',
  `status` int NULL DEFAULT 1 COMMENT '用户状态：1-正常，0-禁用，2-锁定',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `kin_id`(`kin_id` ASC) USING BTREE,
  INDEX `staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `fk_user_role`(`role_id` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_user_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_kin` FOREIGN KEY (`kin_id`) REFERENCES `kin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangdaye', '123456', 1, NULL, NULL, 1, 1, '2025-02-26 04:06:56', '2025-02-26 04:20:13');
INSERT INTO `user` VALUES (2, 'liayi', '123456', NULL, 1, NULL, 2, 1, '2025-02-26 04:06:56', '2025-02-26 04:20:14');
INSERT INTO `user` VALUES (3, 'chendy', '123456', NULL, NULL, 1, 3, 1, '2025-02-26 04:06:56', '2025-02-26 04:20:15');
INSERT INTO `user` VALUES (4, 'admin', '123456', NULL, NULL, NULL, 4, 1, '2025-02-26 04:06:56', '2025-02-26 04:20:19');

SET FOREIGN_KEY_CHECKS = 1;
