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

 Date: 05/03/2025 17:30:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_participants
-- ----------------------------
DROP TABLE IF EXISTS `activity_participants`;
CREATE TABLE `activity_participants`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参与ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-已报名，1-已签到，2-已取消',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_elder`(`activity_id` ASC, `elder_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动参与表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_participants
-- ----------------------------

-- ----------------------------
-- Table structure for community_activities
-- ----------------------------
DROP TABLE IF EXISTS `community_activities`;
CREATE TABLE `community_activities`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动地点',
  `max_participants` int NULL DEFAULT NULL COMMENT '最大参与人数',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organizer_id`(`organizer_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_activities
-- ----------------------------

-- ----------------------------
-- Table structure for elder
-- ----------------------------
DROP TABLE IF EXISTS `elder`;
CREATE TABLE `elder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '老人唯一标识ID，主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人姓名',
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人性别',
  `birthday` datetime(6) NULL DEFAULT NULL COMMENT '老人出生日期',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人身份证号码，唯一',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人家庭住址',
  `emergency_contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人电话',
  `health_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人健康状况',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人头像URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE COMMENT '身份证号码唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '老人基本信息表，存储老人的个人信息、联系方式、健康状况等' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder
-- ----------------------------
INSERT INTO `elder` VALUES (1, '张大爷', 'male', '1940-05-15 00:00:00.000000', 84, '110101194005151234', '13812345678', '北京市朝阳区某小区1号楼101室', '李长娥', '13912345678', '高血压，糖尿病', '/avatar/zhangdaye.jpg', '2025-02-26 04:02:18', '2025-03-05 16:07:07', '喜欢跳广场舞');
INSERT INTO `elder` VALUES (2, '王奶奶', 'female', '1938-10-20 00:00:00.000000', 86, '110102193810205678', '13712345678', '上海市浦东新区某小区2号楼202室', '赵万达', '13612345678', '关节炎', '/avatar/wangnainai.jpg', '2025-02-26 04:02:18', '2025-03-05 16:05:45', '喜欢跳广场舞');
INSERT INTO `elder` VALUES (3, '刘大妈', 'female', '1935-03-12 00:00:00.000000', 89, '110103193503129876', '13687654321', '北京市西城区某小区5号楼505室', '刘小丽', '13587654321', '风湿性关节炎', '/avatar/liudama.jpg', '2025-03-03 15:20:00', '2025-03-05 16:05:45', '喜欢养花');
INSERT INTO `elder` VALUES (4, '孙爷爷', 'male', '1930-09-28 00:00:00.000000', 94, '110104193009286543', '13412348765', '上海市闵行区某小区6号楼606室', '孙小刚', '13312348765', '帕金森病', '/avatar/sungyeye.jpg', '2025-03-03 15:20:00', '2025-03-05 16:05:45', '喜欢下象棋');
INSERT INTO `elder` VALUES (5, '赵老太', 'female', '1932-07-10 00:00:00.000000', 92, '110105193207107890', '13512345678', '北京市海淀区某小区3号楼303室', '赵小明', '13212345678', '心脏病', '/avatar/zhaolaotai.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢听戏');
INSERT INTO `elder` VALUES (6, '钱大爷', 'male', '1937-12-05 00:00:00.000000', 87, '110106193712054321', '13312345678', '上海市徐汇区某小区4号楼404室', '钱小红', '13112345678', '高血压', '/avatar/qiandayie.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢看报纸');
INSERT INTO `elder` VALUES (7, '孙奶奶', 'female', '1939-06-22 00:00:00.000000', 85, '110107193906228765', '13012345678', '广州市天河区某小区7号楼707室', '孙小花', '13987654321', '糖尿病', '/avatar/sunnainai.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢织毛衣');
INSERT INTO `elder` VALUES (8, '李大爷', 'male', '1933-04-18 00:00:00.000000', 91, '110108193304182345', '13887654321', '深圳市南山区某小区8号楼808室', '李小刚', '13787654321', '关节炎', '/avatar/lidayie.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢遛鸟');
INSERT INTO `elder` VALUES (9, '周老太', 'female', '1936-11-08 00:00:00.000000', 88, '110109193611086789', '13611112222', '南京市鼓楼区某小区9号楼909室', '周小美', '13511112222', '风湿病', '/avatar/zhoulaotai.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢跳舞');
INSERT INTO `elder` VALUES (10, '吴大爷', 'male', '1931-08-25 00:00:00.000000', 93, '110110193108253456', '13422223333', '杭州市西湖区某小区10号楼1010室', '吴小强', '13322223333', '心脏病', '/avatar/wudayie.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢钓鱼');
INSERT INTO `elder` VALUES (11, '郑奶奶', 'female', '1938-02-14 00:00:00.000000', 86, '110111193802147890', '13233334444', '成都市武侯区某小区11号楼1111室', '郑小丽', '13133334444', '糖尿病', '/avatar/zhengnainai.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢唱歌');
INSERT INTO `elder` VALUES (12, '冯大爷', 'male', '1934-09-03 00:00:00.000000', 90, '110112193409034567', '13044445555', '武汉市江汉区某小区12号楼1212室', '冯小刚', '13944445555', '高血压', '/avatar/fengdayie.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢种花');
INSERT INTO `elder` VALUES (13, '陈老太', 'female', '1937-05-20 00:00:00.000000', 87, '110113193705208901', '13855556666', '重庆市渝中区某小区13号楼1313室', '陈小美', '13755556666', '关节炎', '/avatar/chenlaotai.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢看电影');
INSERT INTO `elder` VALUES (14, '杨大爷', 'male', '1932-12-10 00:00:00.000000', 92, '110114193212105678', '13666667777', '西安市雁塔区某小区14号楼1414室', '杨小强', '13566667777', '风湿病', '/avatar/yangdayie.jpg', '2025-03-05 17:22:38', '2025-03-05 17:22:38', '喜欢书法');

-- ----------------------------
-- Table structure for health_records
-- ----------------------------
DROP TABLE IF EXISTS `health_records`;
CREATE TABLE `health_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `blood_pressure` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '血压值',
  `heart_rate` int NULL DEFAULT NULL COMMENT '心率',
  `blood_sugar` decimal(5, 2) NULL DEFAULT NULL COMMENT '血糖值',
  `temperature` decimal(4, 1) NULL DEFAULT NULL COMMENT '体温',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '症状描述',
  `medication` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用药情况',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `recorder_id` bigint NOT NULL COMMENT '记录人ID',
  `record_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '记录类型',
  `symptoms_record_time` datetime NULL DEFAULT NULL COMMENT '症状记录时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_recorder_id`(`recorder_id` ASC) USING BTREE,
  CONSTRAINT `fk_health_records_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_records
-- ----------------------------
INSERT INTO `health_records` VALUES (1, 1, '140/90', 78, 8.50, 36.5, 65.00, 170.00, '头晕，胸闷', '降压药，降糖药', '2025-03-04 08:00:00', 101, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (2, 2, '130/80', 75, 6.20, 36.8, 60.00, 165.00, '关节疼痛', '止痛药', '2025-03-04 09:00:00', 102, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (3, 3, '120/70', 80, 7.00, 36.6, 55.00, 160.00, '关节肿胀', '消炎药', '2025-03-04 10:00:00', 103, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (4, 4, '150/95', 85, 9.00, 37.0, 70.00, 175.00, '手脚颤抖', '帕金森病药物', '2025-03-04 11:00:00', 104, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (5, 5, '135/85', 77, 6.80, 36.7, 58.00, 163.00, '关节疼痛', '止痛药', '2025-03-04 12:00:00', 105, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (6, 6, '145/92', 82, 8.80, 36.9, 68.00, 172.00, '胸闷，气短', '心脏病药物', '2025-03-04 13:00:00', 106, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (7, 7, '125/75', 79, 7.20, 36.6, 57.00, 162.00, '口渴，多尿', '降糖药', '2025-03-04 14:00:00', 107, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (8, 8, '155/100', 88, 9.50, 37.2, 72.00, 178.00, '头晕，乏力', '降压药', '2025-03-04 15:00:00', 108, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (9, 9, '132/82', 76, 6.50, 36.8, 62.00, 166.00, '关节疼痛', '消炎药', '2025-03-04 16:00:00', 109, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (10, 10, '148/98', 84, 9.20, 37.1, 71.00, 176.00, '手脚颤抖', '帕金森病药物', '2025-03-04 17:00:00', 110, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (11, 11, '140/90', 78, 8.50, 36.5, 65.00, 170.00, '头晕，胸闷', '降压药，降糖药', '2025-03-04 08:00:00', 101, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (12, 12, '130/80', 75, 6.20, 36.8, 60.00, 165.00, '关节疼痛', '止痛药', '2025-03-04 09:00:00', 102, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (13, 13, '120/70', 80, 7.00, 36.6, 55.00, 160.00, '关节肿胀', '藥品', '2025-03-04 10:00:00', 103, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');
INSERT INTO `health_records` VALUES (14, 14, '150/95', 85, 9.00, 37.0, 70.00, 175.00, '手脚颤抖', '帕金森病药物', '2025-03-04 11:00:00', 104, '日常监测', NULL, '2025-03-05 17:23:17', '2025-03-05 17:23:17');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '老人家属表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kin
-- ----------------------------
INSERT INTO `kin` VALUES (1, '李长娥', '13912345678', '北京市朝阳区某小区1号楼101室', '女儿', 1, '2025-02-26 04:02:37', '2025-02-26 04:23:41', '无');
INSERT INTO `kin` VALUES (2, '赵万达', '13612345678', '上海市浦东新区某小区2号楼202室', '儿子', 2, '2025-02-26 04:02:37', '2025-02-26 04:23:03', '无');
INSERT INTO `kin` VALUES (3, '刘小丽', '13587654321', '北京市西城区某小区5号楼505室', '女儿', 3, '2025-03-03 15:21:00', '2025-03-03 15:21:00', '无');
INSERT INTO `kin` VALUES (4, '孙小刚', '13312348765', '上海市闵行区某小区6号楼606室', '儿子', 4, '2025-03-03 15:21:00', '2025-03-03 15:21:00', '无');

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
-- Table structure for service_appointments
-- ----------------------------
DROP TABLE IF EXISTS `service_appointments`;
CREATE TABLE `service_appointments`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `staff_id` bigint NULL DEFAULT NULL COMMENT '服务人员ID',
  `appointment_time` datetime NOT NULL COMMENT '预约时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_appointments
-- ----------------------------

-- ----------------------------
-- Table structure for services
-- ----------------------------
DROP TABLE IF EXISTS `services`;
CREATE TABLE `services`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务类别',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务描述',
  `duration` int NOT NULL COMMENT '服务时长(分钟)',
  `price` decimal(10, 2) NOT NULL COMMENT '服务价格',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of services
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区工作人员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES (1, '陈医生', '13512345678', '北京市朝阳区某小区3号楼303室', '医疗部', '医生', '2025-02-26 04:02:58', '2025-02-26 04:02:58', '无');
INSERT INTO `staff` VALUES (2, '孙护士', '13412345678', '上海市浦东新区某小区4号楼404室', '护理部', '护士', '2025-02-26 04:02:58', '2025-02-26 04:02:58', '无');
INSERT INTO `staff` VALUES (3, '赵主任', '13212349876', '北京市东城区某小区7号楼707室', '管理部', '主任', '2025-03-03 15:22:00', '2025-03-03 15:22:00', '负责社区日常管理');
INSERT INTO `staff` VALUES (4, '钱社工', '13112349876', '上海市宝山区某小区8号楼808室', '社工部', '社工', '2025-03-03 15:22:00', '2025-03-03 15:22:00', '负责社区活动组织');

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangdaye', '123456', 1, NULL, NULL, 1, 1, '2025-02-26 04:06:56', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (2, 'liayi', '123456', NULL, 1, NULL, 2, 1, '2025-02-26 04:06:56', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (3, 'chendy', '123456', NULL, NULL, 1, 3, 1, '2025-02-26 04:06:56', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (4, 'admin', '123456', NULL, NULL, NULL, 4, 1, '2025-02-26 04:06:56', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (5, 'liudama', '123456', 3, NULL, NULL, 1, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (6, 'liuxl', '123456', NULL, 3, NULL, 2, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (7, 'zhaozr', '123456', NULL, NULL, 3, 3, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (8, 'sungyeye', '123456', 4, NULL, NULL, 1, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (9, 'sunxg', '123456', NULL, 4, NULL, 2, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (10, 'qiansg', '123456', NULL, NULL, 4, 3, 1, '2025-03-03 15:23:00', '2025-03-05 15:27:40');
INSERT INTO `user` VALUES (13, 'ikun', '123456Ad', NULL, NULL, NULL, 3, 1, '2025-03-05 02:21:59', '2025-03-05 02:21:59');

SET FOREIGN_KEY_CHECKS = 1;
