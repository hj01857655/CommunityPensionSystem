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

 Date: 31/03/2025 17:03:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动类型（字典类型：activity_type）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动地点',
  `max_participants` int NULL DEFAULT NULL COMMENT '最大参与人数',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organizer_id`(`organizer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (1, '社区烧烤活动', '7', '欢迎大家参加本次社区烧烤活动，一起享受美食、聊天、玩游戏。', '2023-05-01 18:00:00', '2023-05-01 21:00:00', '社区广场', 100, 1, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (2, '社区健康讲座', '2', '本次讲座将邀请专业医生为大家讲解如何保持健康的生活方式。', '2023-05-10 19:30:00', '2023-05-10 21:00:00', '社区活动中心', 50, 2, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (3, '社区植树活动', '4', '让我们一起为社区种下更多绿色植被,共建美丽家园。', '2023-05-15 09:00:00', '2023-05-15 12:00:00', '社区公园', 80, 3, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (4, '社区亲子游戏日', '9', '孩子们快来参加这次有趣的亲子游戏活动吧!', '2023-05-20 14:00:00', '2023-05-20 17:00:00', '社区活动中心', 60, 4, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (5, '社区篮球赛', '3', '欢迎大家参加本次社区篮球赛,展现你的球技!', '2023-05-25 19:00:00', '2023-05-25 21:30:00', '社区体育馆', 40, 5, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (6, '社区书籍交换会', '9', '把你家里的旧书带来,和其他人交换心仪的新书吧。', '2023-06-01 14:00:00', '2023-06-01 17:00:00', '社区图书馆', 30, 6, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (7, '社区环保讲座', '8', '让我们一起学习如何在日常生活中实践环保理念。', '2023-06-05 19:30:00', '2023-06-05 21:00:00', '社区活动中心', 50, 7, 1, '2025-03-13 18:16:28', '2025-03-30 19:22:25');
INSERT INTO `activities` VALUES (8, '社区DIY手工坊', '9', '来学习制作各种有趣的手工艺品吧!', '2023-06-10 14:00:00', '2023-06-10 17:00:00', '社区活动中心', 40, 8, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (9, '社区烹饪课', '9', '邀请专业厨师为大家讲解美味佳肴的制作方法。', '2023-06-15 19:00:00', '2023-06-15 21:30:00', '社区活动中心', 30, 9, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (10, '社区户外拓展', '9', '一起来体验户外拓展活动,增强团队合作能力。', '2023-06-20 09:00:00', '2023-06-20 16:00:00', '社区公园', 60, 10, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (11, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', '2023-06-25 19:30:00', '2023-06-25 21:30:00', '社区文化中心', 80, 1, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (12, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', '2023-07-01 14:00:00', '2023-07-01 17:00:00', '社区文化中心', 50, 2, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (13, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', '2023-07-05 19:00:00', '2023-07-05 21:00:00', '社区活动中心', 40, 3, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (14, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', '2023-07-10 09:00:00', '2023-07-10 12:00:00', '社区体育场', 80, 4, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (15, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', '2023-07-15 19:00:00', '2023-07-15 21:30:00', '社区文化中心', 60, 5, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (16, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', '2023-07-20 14:00:00', '2023-07-20 17:00:00', '社区公园', 30, 6, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (17, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', '2023-07-25 19:30:00', '2023-07-25 21:00:00', '社区图书馆', 20, 7, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (18, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', '2023-08-01 14:00:00', '2023-08-01 17:00:00', '社区文化中心', 50, 8, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (19, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', '2023-08-05 19:00:00', '2023-08-05 21:30:00', '社区活动中心', 40, 9, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (20, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', '2023-08-10 09:00:00', '2023-08-10 16:00:00', '社区公园', 60, 10, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (21, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', '2023-06-25 19:30:00', '2023-06-25 21:30:00', '社区文化中心', 80, 1, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (22, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', '2023-07-01 14:00:00', '2023-07-01 17:00:00', '社区文化中心', 50, 2, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (23, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', '2023-07-05 19:00:00', '2023-07-05 21:00:00', '社区活动中心', 40, 3, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (24, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', '2023-07-10 09:00:00', '2023-07-10 12:00:00', '社区体育场', 80, 4, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (25, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', '2023-07-15 19:00:00', '2023-07-15 21:30:00', '社区文化中心', 60, 5, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (26, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', '2023-07-20 14:00:00', '2023-07-20 17:00:00', '社区公园', 30, 6, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (27, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', '2023-07-25 19:30:00', '2023-07-25 21:00:00', '社区图书馆', 20, 7, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (28, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', '2023-08-01 14:00:00', '2023-08-01 17:00:00', '社区文化中心', 50, 8, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (29, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', '2023-08-05 19:00:00', '2023-08-05 21:30:00', '社区活动中心', 40, 9, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (30, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', '2023-08-10 09:00:00', '2023-08-10 16:00:00', '社区公园', 60, 10, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');

-- ----------------------------
-- Table structure for activityparticipate
-- ----------------------------
DROP TABLE IF EXISTS `activityparticipate`;
CREATE TABLE `activityparticipate`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参与ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-已报名，1-已签到，2-已取消',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_elder`(`activity_id` ASC, `elder_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动参与表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activityparticipate
-- ----------------------------
INSERT INTO `activityparticipate` VALUES (1, 101, 1, 0, NULL, 0);
INSERT INTO `activityparticipate` VALUES (2, 102, 2, 1, '2023-04-15 10:30:00', 0);
INSERT INTO `activityparticipate` VALUES (3, 103, 3, 0, NULL, 0);
INSERT INTO `activityparticipate` VALUES (4, 104, 4, 2, '2023-04-16 14:00:00', 0);
INSERT INTO `activityparticipate` VALUES (5, 105, 5, 0, NULL, 0);
INSERT INTO `activityparticipate` VALUES (6, 106, 6, 1, '2023-04-17 09:45:00', 0);
INSERT INTO `activityparticipate` VALUES (7, 107, 7, 0, NULL, 0);
INSERT INTO `activityparticipate` VALUES (8, 108, 8, 2, '2023-04-18 16:20:00', 0);
INSERT INTO `activityparticipate` VALUES (9, 109, 9, 0, NULL, 0);
INSERT INTO `activityparticipate` VALUES (10, 110, 10, 1, '2023-04-19 11:00:00', 0);
INSERT INTO `activityparticipate` VALUES (11, 1, 101, 1, '2023-10-01 08:30:00', 0);
INSERT INTO `activityparticipate` VALUES (12, 1, 102, 1, '2023-10-01 08:45:00', 0);
INSERT INTO `activityparticipate` VALUES (13, 1, 103, 1, '2023-10-01 09:00:00', 0);

-- ----------------------------
-- Table structure for activitysignin
-- ----------------------------
DROP TABLE IF EXISTS `activitysignin`;
CREATE TABLE `activitysignin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `sign_in_time` datetime NOT NULL COMMENT '签到时间',
  `sign_out_time` datetime NULL DEFAULT NULL COMMENT '签退时间',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_elder`(`activity_id` ASC, `elder_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  CONSTRAINT `fk_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_elder_id` FOREIGN KEY (`elder_id`) REFERENCES `activityparticipate` (`elder_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动签到表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activitysignin
-- ----------------------------
INSERT INTO `activitysignin` VALUES (4, 1, 101, '2023-10-01 08:30:00', '2023-10-01 11:00:00', '无', 0);
INSERT INTO `activitysignin` VALUES (5, 1, 102, '2023-10-01 08:45:00', '2023-10-01 11:15:00', '无', 0);
INSERT INTO `activitysignin` VALUES (6, 1, 103, '2023-10-01 09:00:00', '2023-10-01 11:30:00', '无', 0);

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '初始化密码 123456');
INSERT INTO `config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `config` VALUES (100, '主题颜色', 'sys.index.theme', '#11A983', 'Y', 'admin', '2023-04-22 00:57:18', 'admin', '2023-04-22 00:58:23', NULL);
INSERT INTO `config` VALUES (101, '开启TopNav', 'sys.index.topNav', 'true', 'Y', 'admin', '2023-04-22 00:58:59', '', NULL, NULL);
INSERT INTO `config` VALUES (102, '开启Tags-Views', 'sys.index.tagsView', 'true', 'Y', 'admin', '2023-04-22 00:59:40', '', NULL, NULL);
INSERT INTO `config` VALUES (103, '显示Logo', 'sys.index.sidebarLogo', 'true', 'Y', 'admin', '2023-04-22 01:00:20', '', NULL, NULL);
INSERT INTO `config` VALUES (104, '固定Header', 'sys.index.fixedHeader', 'true', 'Y', 'admin', '2023-04-22 01:00:53', '', NULL, NULL);
INSERT INTO `config` VALUES (105, '动态标题', 'sys.index.dynamicTitle', 'true', 'Y', 'admin', '2023-04-22 01:01:26', 'admin', '2023-04-22 01:01:41', NULL);

-- ----------------------------
-- Table structure for dict_data
-- ----------------------------
DROP TABLE IF EXISTS `dict_data`;
CREATE TABLE `dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_data
-- ----------------------------
INSERT INTO `dict_data` VALUES (1, 1, '男', '0', 'user_sex', '', '', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '性别男');
INSERT INTO `dict_data` VALUES (2, 2, '女', '1', 'user_sex', '', '', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '性别女');
INSERT INTO `dict_data` VALUES (3, 3, '未知', '2', 'user_sex', '', '', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '性别未知');
INSERT INTO `dict_data` VALUES (4, 1, '显示', '0', 'show_hide', '', 'primary', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '显示菜单');
INSERT INTO `dict_data` VALUES (5, 2, '隐藏', '1', 'show_hide', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '隐藏菜单');
INSERT INTO `dict_data` VALUES (6, 1, '正常', '0', 'normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '正常状态');
INSERT INTO `dict_data` VALUES (7, 2, '停用', '1', 'normal_disable', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '停用状态');
INSERT INTO `dict_data` VALUES (8, 1, '正常', '0', 'job_status', '', 'primary', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '正常状态');
INSERT INTO `dict_data` VALUES (9, 2, '暂停', '1', 'job_status', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '停用状态');
INSERT INTO `dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'job_group', '', '', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '默认分组');
INSERT INTO `dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'job_group', '', '', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '系统分组');
INSERT INTO `dict_data` VALUES (12, 1, '是', 'Y', 'yes_no', '', 'primary', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '系统默认是');
INSERT INTO `dict_data` VALUES (13, 2, '否', 'N', 'yes_no', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '系统默认否');
INSERT INTO `dict_data` VALUES (14, 1, '通知', '1', 'notice_type', '', 'warning', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '通知');
INSERT INTO `dict_data` VALUES (15, 2, '公告', '2', 'notice_type', '', 'success', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '公告');
INSERT INTO `dict_data` VALUES (16, 1, '正常', '0', 'notice_status', '', 'primary', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '正常状态');
INSERT INTO `dict_data` VALUES (17, 2, '关闭', '1', 'notice_status', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '关闭状态');
INSERT INTO `dict_data` VALUES (18, 99, '其他', '0', 'oper_type', '', 'info', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '其他操作');
INSERT INTO `dict_data` VALUES (19, 1, '新增', '1', 'oper_type', '', 'info', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '新增操作');
INSERT INTO `dict_data` VALUES (20, 2, '修改', '2', 'oper_type', '', 'info', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '修改操作');
INSERT INTO `dict_data` VALUES (21, 3, '删除', '3', 'oper_type', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '删除操作');
INSERT INTO `dict_data` VALUES (22, 4, '授权', '4', 'oper_type', '', 'primary', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '授权操作');
INSERT INTO `dict_data` VALUES (23, 5, '导出', '5', 'oper_type', '', 'warning', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '导出操作');
INSERT INTO `dict_data` VALUES (24, 6, '导入', '6', 'oper_type', '', 'warning', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '导入操作');
INSERT INTO `dict_data` VALUES (25, 7, '强退', '7', 'oper_type', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '强退操作');
INSERT INTO `dict_data` VALUES (26, 8, '生成代码', '8', 'oper_type', '', 'warning', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '生成操作');
INSERT INTO `dict_data` VALUES (27, 9, '清空数据', '9', 'oper_type', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:55', '', NULL, '清空操作');
INSERT INTO `dict_data` VALUES (28, 1, '成功', '0', 'common_status', '', 'primary', 'N', '0', 'admin', '2025-03-19 10:18:56', '', NULL, '正常状态');
INSERT INTO `dict_data` VALUES (29, 2, '失败', '1', 'common_status', '', 'danger', 'N', '0', 'admin', '2025-03-19 10:18:56', '', NULL, '停用状态');
INSERT INTO `dict_data` VALUES (30, 1, '文化娱乐', '1', 'activity_type', '', 'primary', 'Y', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '文化娱乐类活动');
INSERT INTO `dict_data` VALUES (31, 2, '健康讲座', '2', 'activity_type', '', 'success', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '健康知识讲座类活动');
INSERT INTO `dict_data` VALUES (32, 3, '体育健身', '3', 'activity_type', '', 'warning', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '体育健身类活动');
INSERT INTO `dict_data` VALUES (33, 4, '志愿服务', '4', 'activity_type', '', 'info', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '志愿服务类活动');
INSERT INTO `dict_data` VALUES (34, 5, '节日庆祝', '5', 'activity_type', '', 'danger', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '节日庆祝类活动');
INSERT INTO `dict_data` VALUES (35, 6, '技能培训', '6', 'activity_type', '', 'primary', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '技能培训类活动');
INSERT INTO `dict_data` VALUES (36, 7, '社交联谊', '7', 'activity_type', '', 'success', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '社交联谊类活动');
INSERT INTO `dict_data` VALUES (37, 8, '公益慈善', '8', 'activity_type', '', 'warning', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '公益慈善类活动');
INSERT INTO `dict_data` VALUES (38, 9, '其他活动', '9', 'activity_type', '', 'info', 'N', '0', 'admin', '2025-03-30 01:41:55', '', NULL, '其他类型活动');

-- ----------------------------
-- Table structure for dict_type
-- ----------------------------
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES (1, '用户性别', 'user_sex', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '用户性别列表');
INSERT INTO `dict_type` VALUES (2, '菜单状态', 'show_hide', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '菜单状态列表');
INSERT INTO `dict_type` VALUES (3, '系统开关', 'normal_disable', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '系统开关列表');
INSERT INTO `dict_type` VALUES (6, '系统是否', 'yes_no', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '系统是否列表');
INSERT INTO `dict_type` VALUES (7, '通知类型', 'notice_type', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '通知类型列表');
INSERT INTO `dict_type` VALUES (8, '通知状态', 'notice_status', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '通知状态列表');
INSERT INTO `dict_type` VALUES (9, '操作类型', 'oper_type', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '操作类型列表');
INSERT INTO `dict_type` VALUES (10, '系统状态', 'common_status', '1', 'admin', '2025-03-19 10:20:20', '', NULL, '登录状态列表');
INSERT INTO `dict_type` VALUES (11, '服务类型', 'service_type', '1', 'admin', '2025-03-30 01:41:22', '', '2025-03-30 01:41:22', '服务类型列表');
INSERT INTO `dict_type` VALUES (12, '活动类型', 'activity_type', '1', 'admin', '2025-03-30 01:41:55', '', '2025-03-30 01:41:55', '活动类型列表');

-- ----------------------------
-- Table structure for elder_kin_relation
-- ----------------------------
DROP TABLE IF EXISTS `elder_kin_relation`;
CREATE TABLE `elder_kin_relation`  (
  `elder_id` bigint NOT NULL COMMENT '老人用户ID',
  `kin_id` bigint NOT NULL COMMENT '家属用户ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '关系类型(父子/母女等)',
  PRIMARY KEY (`elder_id`, `kin_id`) USING BTREE,
  INDEX `idx_kin_id`(`kin_id` ASC) USING BTREE COMMENT '家属ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder_kin_relation
-- ----------------------------

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
  `bmi` decimal(5, 2) NULL DEFAULT NULL COMMENT 'BMI指数',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '病史',
  `allergy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '过敏史',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '症状描述',
  `symptoms_record_time` datetime NULL DEFAULT NULL COMMENT '症状记录时间',
  `medication` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用药情况',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `recorder_id` bigint NOT NULL COMMENT '记录人ID',
  `record_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '记录类型',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_recorder_id`(`recorder_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_records
-- ----------------------------

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2000 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '', 1, 1, 'M', '1', '0', '', 'system', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '系统管理');
INSERT INTO `menu` VALUES (2, '数据分析看板', 0, 2, 'analysis', NULL, '', '', 1, 1, 'M', '1', '0', '', 'analysis', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '数据分析看板');
INSERT INTO `menu` VALUES (3, '服务预约', 0, 3, 'services', NULL, '', '', 1, 1, 'M', '1', '0', '', 'service', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '服务预约管理');
INSERT INTO `menu` VALUES (4, '健康监测', 0, 4, 'health', NULL, '', '', 1, 1, 'M', '1', '0', '', 'health', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '健康监测管理');
INSERT INTO `menu` VALUES (5, '社区活动', 0, 5, 'activity', NULL, NULL, '', 1, 1, 'M', '1', '0', NULL, '#', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '');
INSERT INTO `menu` VALUES (6, '通知公告', 0, 6, 'notice', NULL, NULL, '', 1, 1, 'C', '1', '0', NULL, '#', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '');
INSERT INTO `menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 1, 'C', '1', '0', 'system:user:list', 'AddLocation', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '用户管理菜单');
INSERT INTO `menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 1, 'C', '1', '0', 'system:role:list', 'peoples', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '角色管理菜单');
INSERT INTO `menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 1, 'C', '1', '0', 'system:menu:list', 'tree-table', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '菜单管理菜单');
INSERT INTO `menu` VALUES (103, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 1, 'C', '1', '0', 'system:dict:list', 'dict', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '字典管理菜单');
INSERT INTO `menu` VALUES (104, '系统设置', 1, 9, 'config', 'system/config/index', '', '', 1, 1, 'M', '1', '0', '', 'log', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '日志管理菜单');
INSERT INTO `menu` VALUES (107, '仪表盘', 2, 1, 'dashboard', 'analysis/dashboard/index', '', '', 1, 1, 'C', '1', '0', 'monitor:online:list', 'online', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '仪表盘');
INSERT INTO `menu` VALUES (108, '服务项目管理', 3, 0, 'service', 'services/service', NULL, '', 1, 1, 'C', '1', '0', 'services:service:list', 'service', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '');
INSERT INTO `menu` VALUES (109, '服务工单管理', 3, 1, 'appointment', 'services/appointment', NULL, '', 1, 1, 'C', '1', '0', NULL, 'service', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '');
INSERT INTO `menu` VALUES (110, '服务评价管理', 3, 2, 'evaluation', 'services/evaluation', NULL, '', 1, 1, 'C', '1', '0', NULL, 'service', 'admin', '2025-03-20 03:44:24', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:query', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:add', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:edit', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:remove', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:export', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:import', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 1, 'F', '1', '0', 'system:user:resetPwd', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 1, 'F', '1', '0', 'system:role:query', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 1, 'F', '1', '0', 'system:role:add', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 1, 'F', '1', '0', 'system:role:edit', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 1, 'F', '1', '0', 'system:role:remove', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 1, 'F', '1', '0', 'system:role:export', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 1, 'F', '1', '0', 'system:menu:query', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 1, 'F', '1', '0', 'system:menu:add', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 1, 'F', '1', '0', 'system:menu:edit', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 1, 'F', '1', '0', 'system:menu:remove', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1016, '字典查询', 103, 1, '', '', '', '', 1, 1, 'F', '1', '0', 'system:dept:query', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1017, '字典新增', 103, 2, '', '', '', '', 1, 1, 'F', '1', '0', 'system:dept:add', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1018, '字典修改', 103, 3, '', '', '', '', 1, 1, 'F', '1', '0', 'system:dept:edit', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1019, '字典删除', 103, 4, '', '', '', '', 1, 1, 'F', '1', '0', 'system:dept:remove', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');
INSERT INTO `menu` VALUES (1020, '字典导出', 103, 5, '', '', '', '', 1, 1, 'F', '1', '0', 'system:post:query', '#', 'admin', '2025-03-20 03:44:25', 'admin', NULL, '');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布 2-已撤回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, '社区健康讲座通知', '计划于下月举办老年人健康知识讲座，具体时间待定', 0, '2023-03-27 09:00:00', '2025-03-28 16:40:54', '2025-03-28 16:41:46');
INSERT INTO `notification` VALUES (2, '端午节活动筹备', '端午节包粽子活动正在筹备中，需要志愿者报名', 0, '2023-03-27 10:00:00', '2025-03-28 16:41:56', '2023-05-15 14:30:00');
INSERT INTO `notification` VALUES (3, '系统维护通知', '系统将于今晚23:00-24:00进行维护升级，期间暂停服务', 1, '2023-03-27 11:00:00', '2025-03-28 16:42:07', '2023-05-20 15:00:00');
INSERT INTO `notification` VALUES (4, '老年体检安排', '6月1日-6月5日社区医院为65岁以上老人提供免费体检', 1, '2023-03-27 11:00:00', '2025-03-28 16:42:13', '2023-05-25 09:00:00');
INSERT INTO `notification` VALUES (5, '防诈骗知识讲座', '5月30日下午2点社区活动中心举办老年人防诈骗讲座', 1, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-28 10:30:00');
INSERT INTO `notification` VALUES (6, '错误通知示例', '此通知内容有误，已撤回', 2, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-18 08:00:00');
INSERT INTO `notification` VALUES (7, '活动取消通知', '原定于5月22日的书法班因故取消，时间另行通知', 2, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-21 13:00:00');
INSERT INTO `notification` VALUES (8, '重要：医保政策更新', '2023年最新医保报销政策已更新，请及时查看', 1, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-06-01 09:00:00');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `idx_role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '老人', 'elder', 1, '2', 1, '1', '0', 'admin', '2025-03-19 15:57:06', '', NULL, '老人');
INSERT INTO `role` VALUES (2, '家属', 'kin', 2, '2', 1, '1', '0', 'admin', '2025-03-19 15:57:22', '', NULL, '家属');
INSERT INTO `role` VALUES (3, '社区工作人员', 'staff', 3, '2', 1, '1', '0', 'admin', '2025-03-19 15:57:22', '', NULL, '社区工作人员');
INSERT INTO `role` VALUES (4, '管理员', 'admin', 4, '1', 1, '1', '0', 'admin', '2025-03-19 15:57:50', '', NULL, '管理员');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_menu
-- ----------------------------

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

-- ----------------------------
-- Table structure for service_order
-- ----------------------------
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '预约用户',
  `service_item_id` bigint NOT NULL COMMENT '服务项目',
  `status` tinyint NOT NULL COMMENT '状态(0-待审核 1-已派单 2-服务中 3-已完成)',
  `apply_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请理由',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `schedule_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `actual_duration` int NULL DEFAULT NULL COMMENT '实际服务时长(分钟)',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `createBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `service_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_order
-- ----------------------------

-- ----------------------------
-- Table structure for service_review
-- ----------------------------
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单',
  `user_id` bigint NOT NULL COMMENT '评价用户',
  `rating` tinyint NOT NULL COMMENT '评分(1-5星)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `review_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '官方回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `createBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `service_review_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `service_review_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_review
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '123456' COMMENT '密码（加密存储）',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` enum('男','女','未知') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '未知' COMMENT '性别',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '13800000004' COMMENT '电话号码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '电子邮件',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户地址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态：1-正常，0-禁用，2-锁定',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '社区工作人员所在部门',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '社区工作人员岗位',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人身份证号码，唯一',
  `birthday` date NULL DEFAULT NULL COMMENT '老人出生日期',
  `age` int NULL DEFAULT NULL COMMENT '老人年龄',
  `emergency_contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人电话',
  `health_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '老人健康状况',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_user_status`(`is_active` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '超级管理员', '女', '13800000001', 'bob.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2025-02-26 04:06:56', NULL, '2025-03-27 22:16:52', '');
INSERT INTO `user` VALUES (2, 'Ldamao', '123456', '刘大毛', '女', '13800000002', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '340881199005204216', '1990-05-20', 35, '张伟', '13812345678', '中等，需定期监测血压、血糖，并注意饮食控制。', 'staff', '2025-03-22 19:46:06', NULL, '2025-03-28 15:22:26', '');
INSERT INTO `user` VALUES (3, 'Lxiaomao', '123456', '刘小毛', '男', '13800000004', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '', NULL, NULL, '', '', '', 'staff', '2025-03-27 15:38:04', NULL, '2025-03-28 15:22:21', '');
INSERT INTO `user` VALUES (25, 'Lshumei', '123456', '刘淑梅', '未知', '15349545835', 'yopsu@mailto.plus', NULL, NULL, 1, '', '', '12345619900101123X', '1990-01-01', 35, '王小明', '13987654321  ', '慢性咳嗽', NULL, NULL, NULL, NULL, '');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 4);
INSERT INTO `user_role` VALUES (2, 1);
INSERT INTO `user_role` VALUES (3, 2);
INSERT INTO `user_role` VALUES (25, 1);

-- ----------------------------
-- Table structure for warning_record
-- ----------------------------
DROP TABLE IF EXISTS `warning_record`;
CREATE TABLE `warning_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `user_id` bigint NOT NULL COMMENT '用户ID，关联用户表',
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预警类型（如血压异常）',
  `warning_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预警信息',
  `warning_time` datetime NULL DEFAULT NULL COMMENT '预警时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `warning_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warning_record
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
