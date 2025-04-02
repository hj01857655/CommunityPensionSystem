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

 Date: 31/03/2025 16:44:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for service_item
-- ----------------------------
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item`  (
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `service_type` enum('medical','cleaning','repair') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务描述',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '服务价格',
  `duration` int NULL DEFAULT 0 COMMENT '服务时长(分钟)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（0下架/1上架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`service_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_item
-- ----------------------------
INSERT INTO `service_item` VALUES (1001, '上门体检', 'medical', '为行动不便老人提供上门基础体检服务', 150.00, 120, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:19', NULL);
INSERT INTO `service_item` VALUES (1002, '药品代购', 'medical', '帮助老人代购处方药品并送药上门', 20.00, 30, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:21', NULL);
INSERT INTO `service_item` VALUES (1003, '健康咨询', 'medical', '专业医生提供30分钟健康咨询服务', 80.00, 30, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:22', NULL);
INSERT INTO `service_item` VALUES (2001, '全屋清洁', 'cleaning', '包含客厅、卧室、厨房、卫生间全面清洁', 200.00, 180, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:23', NULL);
INSERT INTO `service_item` VALUES (2002, '厨房深度清洁', 'cleaning', '油烟机、灶台等厨房区域专项清洁', 120.00, 90, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:24', NULL);
INSERT INTO `service_item` VALUES (2003, '玻璃清洁', 'cleaning', '室内外窗户玻璃专业清洁', 80.00, 60, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:25', NULL);
INSERT INTO `service_item` VALUES (3001, '水管维修', 'repair', '解决漏水、堵塞等水管问题', 100.00, 60, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:27', NULL);
INSERT INTO `service_item` VALUES (3002, '电路检修', 'repair', '家庭电路安全检查与维修', 150.00, 90, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:27', NULL);
INSERT INTO `service_item` VALUES (3003, '家电维修', 'repair', '冰箱、洗衣机等家电故障维修', 180.00, 120, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:29', NULL);
INSERT INTO `service_item` VALUES (4001, '老人关爱套餐', 'medical', '包含体检+健康咨询+药品代购的套餐服务', 200.00, 180, 1, '2025-03-28 17:00:57', 'admin', NULL, '2025-03-31 16:40:29', '限时特惠套餐');

SET FOREIGN_KEY_CHECKS = 1;
