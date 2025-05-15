/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : community_pension

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 14/05/2025 21:19:33
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
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动封面图片URL',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `register_start_time` datetime NULL DEFAULT NULL COMMENT '报名开始时间',
  `register_end_time` datetime NULL DEFAULT NULL COMMENT '报名结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动地点',
  `max_participants` int NULL DEFAULT NULL COMMENT '最大参与人数',
  `min_participants` int NULL DEFAULT NULL COMMENT '最小参与人数',
  `need_approval` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否需要审核：0-不需要，1-需要',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动标签，多个标签用逗号分隔',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organizer_id`(`organizer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
  INDEX `idx_register_start_time`(`register_start_time` ASC) USING BTREE,
  INDEX `idx_register_end_time`(`register_end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (1, '社区烧烤活动', '7', '欢迎大家参加本次社区烧烤活动，一起享受美食、聊天、玩游戏。', NULL, '2023-05-01 18:00:00', '2023-05-01 21:00:00', NULL, NULL, '社区广场', 100, NULL, 1, NULL, 1, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (2, '社区健康讲座', '2', '本次讲座将邀请专业医生为大家讲解如何保持健康的生活方式。', NULL, '2023-05-10 19:30:00', '2023-05-10 21:00:00', NULL, NULL, '社区活动中心', 50, NULL, 1, NULL, 2, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (3, '社区植树活动', '4', '让我们一起为社区种下更多绿色植被,共建美丽家园。', NULL, '2023-05-15 09:00:00', '2023-05-15 12:00:00', NULL, NULL, '社区公园', 80, NULL, 1, NULL, 3, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (4, '社区亲子游戏日', '9', '孩子们快来参加这次有趣的亲子游戏活动吧!', NULL, '2023-05-20 14:00:00', '2023-05-20 17:00:00', NULL, NULL, '社区活动中心', 60, NULL, 1, NULL, 4, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (5, '社区篮球赛', '3', '欢迎大家参加本次社区篮球赛,展现你的球技!', NULL, '2023-05-25 19:00:00', '2023-05-25 21:30:00', NULL, NULL, '社区体育馆', 40, NULL, 1, NULL, 5, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (6, '社区书籍交换会', '9', '把你家里的旧书带来,和其他人交换心仪的新书吧。', NULL, '2023-06-01 14:00:00', '2023-06-01 17:00:00', NULL, NULL, '社区图书馆', 30, NULL, 1, NULL, 6, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (7, '社区环保讲座', '8', '让我们一起学习如何在日常生活中实践环保理念。', NULL, '2023-06-05 19:30:00', '2023-06-05 21:00:00', NULL, NULL, '社区活动中心', 50, NULL, 1, NULL, 7, 1, '2025-03-13 18:16:28', '2025-03-30 19:22:25');
INSERT INTO `activities` VALUES (8, '社区DIY手工坊', '9', '来学习制作各种有趣的手工艺品吧!', NULL, '2023-06-10 14:00:00', '2023-06-10 17:00:00', NULL, NULL, '社区活动中心', 40, NULL, 1, NULL, 8, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (9, '社区烹饪课', '9', '邀请专业厨师为大家讲解美味佳肴的制作方法。', NULL, '2023-06-15 19:00:00', '2023-06-15 21:30:00', NULL, NULL, '社区活动中心', 30, NULL, 1, NULL, 9, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (10, '社区户外拓展', '9', '一起来体验户外拓展活动,增强团队合作能力。', NULL, '2023-06-20 09:00:00', '2023-06-20 16:00:00', NULL, NULL, '社区公园', 60, NULL, 1, NULL, 10, 1, '2025-03-13 18:16:28', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (11, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', NULL, '2023-06-25 19:30:00', '2023-06-25 21:30:00', NULL, NULL, '社区文化中心', 80, NULL, 1, NULL, 1, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (12, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', NULL, '2023-07-01 14:00:00', '2023-07-01 17:00:00', NULL, NULL, '社区文化中心', 50, NULL, 1, NULL, 2, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (13, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', NULL, '2023-07-05 19:00:00', '2023-07-05 21:00:00', NULL, NULL, '社区活动中心', 40, NULL, 1, NULL, 3, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (14, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', NULL, '2023-07-10 09:00:00', '2023-07-10 12:00:00', NULL, NULL, '社区体育场', 80, NULL, 1, NULL, 4, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (15, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', NULL, '2023-07-15 19:00:00', '2023-07-15 21:30:00', NULL, NULL, '社区文化中心', 60, NULL, 1, NULL, 5, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (16, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', NULL, '2023-07-20 14:00:00', '2023-07-20 17:00:00', NULL, NULL, '社区公园', 30, NULL, 1, NULL, 6, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (17, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', NULL, '2023-07-25 19:30:00', '2023-07-25 21:00:00', NULL, NULL, '社区图书馆', 20, NULL, 1, NULL, 7, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (18, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', NULL, '2023-08-01 14:00:00', '2023-08-01 17:00:00', NULL, NULL, '社区文化中心', 50, NULL, 1, NULL, 8, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (19, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', NULL, '2023-08-05 19:00:00', '2023-08-05 21:30:00', NULL, NULL, '社区活动中心', 40, NULL, 1, NULL, 9, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (20, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', NULL, '2023-08-10 09:00:00', '2023-08-10 16:00:00', NULL, NULL, '社区公园', 60, NULL, 1, NULL, 10, 1, '2025-03-13 18:17:14', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (21, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', NULL, '2023-06-25 19:30:00', '2023-06-25 21:30:00', NULL, NULL, '社区文化中心', 80, NULL, 1, NULL, 1, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (22, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', NULL, '2023-07-01 14:00:00', '2023-07-01 17:00:00', NULL, NULL, '社区文化中心', 50, NULL, 1, NULL, 2, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (23, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', NULL, '2023-07-05 19:00:00', '2023-07-05 21:00:00', NULL, NULL, '社区活动中心', 40, NULL, 1, NULL, 3, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (24, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', NULL, '2023-07-10 09:00:00', '2023-07-10 12:00:00', NULL, NULL, '社区体育场', 80, NULL, 1, NULL, 4, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (25, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', NULL, '2023-07-15 19:00:00', '2023-07-15 21:30:00', NULL, NULL, '社区文化中心', 60, NULL, 1, NULL, 5, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (26, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', NULL, '2023-07-20 14:00:00', '2023-07-20 17:00:00', NULL, NULL, '社区公园', 30, NULL, 1, NULL, 6, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (27, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', NULL, '2023-07-25 19:30:00', '2023-07-25 21:00:00', NULL, NULL, '社区图书馆', 20, NULL, 1, NULL, 7, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (28, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', NULL, '2023-08-01 14:00:00', '2023-08-01 17:00:00', NULL, NULL, '社区文化中心', 50, NULL, 1, NULL, 8, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (29, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', NULL, '2023-08-05 19:00:00', '2023-08-05 21:30:00', NULL, NULL, '社区活动中心', 40, NULL, 1, NULL, 9, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');
INSERT INTO `activities` VALUES (30, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', NULL, '2023-08-10 09:00:00', '2023-08-10 16:00:00', NULL, NULL, '社区公园', 60, NULL, 1, NULL, 10, 1, '2025-03-13 18:17:30', '2025-03-30 19:21:03');

-- ----------------------------
-- Table structure for activity_check_in
-- ----------------------------
DROP TABLE IF EXISTS `activity_check_in`;
CREATE TABLE `activity_check_in`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `register_id` bigint NOT NULL COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `check_in_user_id` bigint NULL DEFAULT NULL COMMENT '签到人ID（老人本人或家属）',
  `check_in_type` tinyint NULL DEFAULT NULL COMMENT '签到类型：0-老人自己签到，1-家属代签到',
  `sign_in_time` datetime NOT NULL COMMENT '签到时间',
  `sign_out_time` datetime NULL DEFAULT NULL COMMENT '签退时间',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_register_id`(`register_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_check_in_user_id`(`check_in_user_id` ASC) USING BTREE,
  INDEX `idx_sign_in_time`(`sign_in_time` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  CONSTRAINT `fk_activity_check_in_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_check_in_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_check_in_register` FOREIGN KEY (`register_id`) REFERENCES `activity_register` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_check_in_user` FOREIGN KEY (`check_in_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动签到表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activity_check_in
-- ----------------------------

-- ----------------------------
-- Table structure for activity_register
-- ----------------------------
DROP TABLE IF EXISTS `activity_register`;
CREATE TABLE `activity_register`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `register_user_id` bigint NULL DEFAULT NULL COMMENT '报名人ID（老人本人或家属）',
  `register_type` tinyint NOT NULL DEFAULT 0 COMMENT '报名类型：0-老人自己报名，1-家属代报名',
  `register_time` datetime NOT NULL COMMENT '报名时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-已取消',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_elder`(`activity_id` ASC, `elder_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_register_user_id`(`register_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_register_time`(`register_time` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  CONSTRAINT `fk_activity_register_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_register_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_register_register_user` FOREIGN KEY (`register_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动报名表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activity_register
-- ----------------------------
INSERT INTO `activity_register` VALUES (14, 21, 2, 2, 0, '2025-05-14 13:24:30', 0, NULL, '2025-04-05 22:22:57', '2025-04-05 22:22:57', 0);
INSERT INTO `activity_register` VALUES (15, 22, 2, 2, 0, '2025-05-12 12:41:23', 3, NULL, '2025-05-12 12:41:23', '2025-05-12 12:41:23', 0);
INSERT INTO `activity_register` VALUES (16, 23, 2, 2, 0, '2025-05-12 19:23:01', 3, NULL, '2025-05-12 19:23:01', '2025-05-12 19:23:01', 0);

-- ----------------------------
-- Table structure for activity_review
-- ----------------------------
DROP TABLE IF EXISTS `activity_review`;
CREATE TABLE `activity_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `register_id` bigint NOT NULL COMMENT '报名ID',
  `rating` int NOT NULL COMMENT '评分（1-5）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `review_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_register_id`(`register_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  CONSTRAINT `fk_activity_review_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_review_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_review_register` FOREIGN KEY (`register_id`) REFERENCES `activity_register` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activity_review
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '初始化密码 123456');
INSERT INTO `config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 'admin', '2025-03-19 10:55:25', '', NULL, '是否开启验证码功能（true开启，false关闭）');
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
) ENGINE = InnoDB AUTO_INCREMENT = 145 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dict_data
-- ----------------------------
INSERT INTO `dict_data` VALUES (1, 1, '男', '0', 'user_sex', '', '', 'Y', '0', 'admin', '2025-03-19 10:18:55', '', '2025-04-01 14:48:01', '性别男');
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
INSERT INTO `dict_data` VALUES (102, 1, '医疗服务', 'medical', 'service_type', '', 'primary', 'Y', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '医疗类服务');
INSERT INTO `dict_data` VALUES (103, 2, '清洁服务', 'cleaning', 'service_type', '', 'success', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '清洁类服务');
INSERT INTO `dict_data` VALUES (104, 3, '维修服务', 'repair', 'service_type', '', 'warning', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '维修类服务');
INSERT INTO `dict_data` VALUES (105, 1, '待审核', '0', 'order_status', '', 'info', 'Y', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单待审核状态');
INSERT INTO `dict_data` VALUES (106, 2, '已派单', '1', 'order_status', '', 'primary', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单已派单状态');
INSERT INTO `dict_data` VALUES (107, 3, '服务中', '2', 'order_status', '', 'warning', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单服务中状态');
INSERT INTO `dict_data` VALUES (108, 4, '已完成', '3', 'order_status', '', 'success', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单已完成状态');
INSERT INTO `dict_data` VALUES (109, 5, '已取消', '4', 'order_status', '', 'danger', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单已取消状态');
INSERT INTO `dict_data` VALUES (110, 6, '已拒绝', '5', 'order_status', '', 'danger', 'N', '0', 'admin', '2025-04-05 02:10:48', 'admin', '2025-04-05 02:10:48', '工单已拒绝状态');
INSERT INTO `dict_data` VALUES (111, 1, '筹备中', '0', 'activity_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, NULL);
INSERT INTO `dict_data` VALUES (112, 2, '报名中', '1', 'activity_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, NULL);
INSERT INTO `dict_data` VALUES (113, 3, '进行中', '2', 'activity_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, NULL);
INSERT INTO `dict_data` VALUES (114, 4, '已结束', '3', 'activity_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, NULL);
INSERT INTO `dict_data` VALUES (115, 5, '已取消', '4', 'activity_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, NULL);
INSERT INTO `dict_data` VALUES (116, 1, '父子', 'father_son', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '父子关系');
INSERT INTO `dict_data` VALUES (117, 2, '母女', 'mother_daughter', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '母女关系');
INSERT INTO `dict_data` VALUES (118, 3, '配偶', 'spouse', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '配偶关系');
INSERT INTO `dict_data` VALUES (119, 4, '子女', 'children', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '子女关系');
INSERT INTO `dict_data` VALUES (120, 5, '兄弟姐妹', 'siblings', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '兄弟姐妹关系');
INSERT INTO `dict_data` VALUES (121, 6, '其他', 'other', 'kinship_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '其他关系');
INSERT INTO `dict_data` VALUES (122, 1, '健康', 'healthy', 'health_level', NULL, NULL, 'Y', '0', '', NULL, '', NULL, '健康');
INSERT INTO `dict_data` VALUES (123, 2, '亚健康', 'subhealthy', 'health_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '亚健康');
INSERT INTO `dict_data` VALUES (124, 3, '慢病', 'chronic', 'health_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '慢性病');
INSERT INTO `dict_data` VALUES (125, 4, '重病', 'serious', 'health_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '重病');
INSERT INTO `dict_data` VALUES (126, 5, '残疾', 'disabled', 'health_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '残疾');
INSERT INTO `dict_data` VALUES (127, 1, '非常满意', '5', 'service_rating', NULL, NULL, 'Y', '0', '', NULL, '', NULL, '5星');
INSERT INTO `dict_data` VALUES (128, 2, '满意', '4', 'service_rating', NULL, NULL, 'N', '0', '', NULL, '', NULL, '4星');
INSERT INTO `dict_data` VALUES (129, 3, '一般', '3', 'service_rating', NULL, NULL, 'N', '0', '', NULL, '', NULL, '3星');
INSERT INTO `dict_data` VALUES (130, 4, '不满意', '2', 'service_rating', NULL, NULL, 'N', '0', '', NULL, '', NULL, '2星');
INSERT INTO `dict_data` VALUES (131, 5, '非常不满意', '1', 'service_rating', NULL, NULL, 'N', '0', '', NULL, '', NULL, '1星');
INSERT INTO `dict_data` VALUES (132, 1, '高风险', 'high', 'elder_risk_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '高风险');
INSERT INTO `dict_data` VALUES (133, 2, '中风险', 'medium', 'elder_risk_level', NULL, NULL, 'N', '0', '', NULL, '', NULL, '中风险');
INSERT INTO `dict_data` VALUES (134, 3, '低风险', 'low', 'elder_risk_level', NULL, NULL, 'Y', '0', '', NULL, '', NULL, '低风险');
INSERT INTO `dict_data` VALUES (135, 1, '身份证', 'id_card', 'id_type', NULL, NULL, 'Y', '0', '', NULL, '', NULL, '身份证');
INSERT INTO `dict_data` VALUES (136, 2, '护照', 'passport', 'id_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '护照');
INSERT INTO `dict_data` VALUES (137, 3, '军官证', 'officer_card', 'id_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '军官证');
INSERT INTO `dict_data` VALUES (138, 4, '港澳通行证', 'hk_macao_permit', 'id_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '港澳通行证');
INSERT INTO `dict_data` VALUES (139, 5, '台胞证', 'taiwan_permit', 'id_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '台胞证');
INSERT INTO `dict_data` VALUES (140, 6, '其他', 'other', 'id_type', NULL, NULL, 'N', '0', '', NULL, '', NULL, '其他证件');
INSERT INTO `dict_data` VALUES (141, 1, '待审核', '0', 'activity_register_status', NULL, NULL, 'Y', '0', '', NULL, '', NULL, '待审核');
INSERT INTO `dict_data` VALUES (142, 2, '已通过', '1', 'activity_register_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, '已通过');
INSERT INTO `dict_data` VALUES (143, 3, '已拒绝', '2', 'activity_register_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, '已拒绝');
INSERT INTO `dict_data` VALUES (144, 4, '已取消', '3', 'activity_register_status', NULL, NULL, 'N', '0', '', NULL, '', NULL, '已取消');

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
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES (1, '用户性别', 'user_sex', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '用户性别列表');
INSERT INTO `dict_type` VALUES (2, '菜单状态', 'show_hide', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '菜单状态列表');
INSERT INTO `dict_type` VALUES (3, '系统开关', 'normal_disable', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '系统开关列表');
INSERT INTO `dict_type` VALUES (6, '系统状态', 'yes_no', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '系统是否列表');
INSERT INTO `dict_type` VALUES (7, '通知类型', 'notice_type', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '通知类型列表');
INSERT INTO `dict_type` VALUES (8, '通知状态', 'notice_status', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '通知状态列表');
INSERT INTO `dict_type` VALUES (9, '操作类型', 'oper_type', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '操作类型列表');
INSERT INTO `dict_type` VALUES (10, '系统状态', 'common_status', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '登录状态列表');
INSERT INTO `dict_type` VALUES (11, '服务类型', 'service_type', '0', 'admin', '2025-04-05 02:09:58', 'admin', '2025-04-05 02:09:58', '服务项目的类型');
INSERT INTO `dict_type` VALUES (12, '活动类型', 'activity_type', '0', 'admin', '2025-05-14 17:23:39', 'admin', '2025-05-14 17:23:39', '活动类型列表');
INSERT INTO `dict_type` VALUES (101, '工单状态', 'order_status', '0', 'admin', '2025-04-05 02:09:40', 'admin', '2025-04-05 02:09:40', '服务工单的状态');
INSERT INTO `dict_type` VALUES (103, '家庭关系类型', 'kinship_type', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '老人和家属的关系类型');
INSERT INTO `dict_type` VALUES (105, '健康等级', 'health_level', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '老人健康状况分级');
INSERT INTO `dict_type` VALUES (106, '服务评价等级', 'service_rating', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '服务评价星级');
INSERT INTO `dict_type` VALUES (107, '老人风险等级', 'elder_risk_level', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '老人健康风险分级');
INSERT INTO `dict_type` VALUES (108, '证件类型', 'id_type', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '用户证件类型');
INSERT INTO `dict_type` VALUES (109, '活动报名状态', 'activity_register_status', '0', 'admin', '2025-05-14 17:28:37', 'admin', '2025-05-14 17:28:37', '活动报名审核状态');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of elder_kin_relation
-- ----------------------------
INSERT INTO `elder_kin_relation` VALUES (2, 3, '父子');
INSERT INTO `elder_kin_relation` VALUES (25, 26, '子女');

-- ----------------------------
-- Table structure for health_alert
-- ----------------------------
DROP TABLE IF EXISTS `health_alert`;
CREATE TABLE `health_alert`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `alert_type` int NOT NULL COMMENT '预警类型：1-血压异常，2-血糖异常，3-心率异常，4-体温异常，5-活动异常，6-用药提醒，7-复查提醒，8-其他异常',
  `alert_level` int NOT NULL COMMENT '预警级别：1-低，2-中，3-高，4-紧急',
  `alert_source` int NOT NULL COMMENT '预警来源：1-系统自动监测，2-穿戴设备，3-人工录入，4-定期检查',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警内容',
  `alert_time` datetime NOT NULL COMMENT '预警时间',
  `status` int NOT NULL DEFAULT 0 COMMENT '处理状态：0-未处理，1-已处理，2-已忽略',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handle_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `related_data_id` bigint NULL DEFAULT NULL COMMENT '相关数据ID(如监测记录ID)',
  `notified` int NOT NULL DEFAULT 0 COMMENT '是否已通知：0-未通知，1-已通知',
  `notify_time` datetime NULL DEFAULT NULL COMMENT '通知时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_alert_time`(`alert_time` ASC) USING BTREE,
  INDEX `idx_alert_level`(`alert_level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_related_data_id`(`related_data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康预警表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_alert
-- ----------------------------

-- ----------------------------
-- Table structure for health_monitoring
-- ----------------------------
DROP TABLE IF EXISTS `health_monitoring`;
CREATE TABLE `health_monitoring`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '监测记录ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `monitoring_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '监测类型（1:血压, 2:血糖, 3:体温, 4:心率, 5:血氧, 6:体重, 7:其他）',
  `monitoring_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '监测值',
  `monitoring_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '监测单位',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备ID',
  `monitoring_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '监测状态(normal/abnormal)',
  `abnormal_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异常等级(low/medium/high)',
  `abnormal_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '异常描述',
  `is_processed` tinyint(1) NULL DEFAULT 0 COMMENT '是否已处理',
  `processed_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `processed_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `processed_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理结果',
  `health_record_id` bigint NULL DEFAULT NULL COMMENT '关联的健康档案ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_monitoring_time`(`monitoring_time` ASC) USING BTREE,
  INDEX `idx_device_id`(`device_id` ASC) USING BTREE,
  INDEX `idx_health_record_id`(`health_record_id` ASC) USING BTREE,
  CONSTRAINT `fk_health_monitoring_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_health_monitoring_health_record` FOREIGN KEY (`health_record_id`) REFERENCES `health_records` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康监测表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_monitoring
-- ----------------------------
INSERT INTO `health_monitoring` VALUES (1, 2, '1', '125/78', 'mmHg', '2025-05-07 09:00:00', NULL, 'normal', NULL, NULL, 0, NULL, NULL, NULL, NULL, '2025-05-06 21:18:04', '2025-05-06 21:18:04');
INSERT INTO `health_monitoring` VALUES (2, 2, '3', '36.8', '°C', '2025-05-07 11:00:00', NULL, 'normal', NULL, NULL, 0, NULL, NULL, NULL, NULL, '2025-05-06 21:18:04', '2025-05-06 21:18:04');
INSERT INTO `health_monitoring` VALUES (3, 2, '2', '6.5', 'mmol/L', '2025-05-08 08:30:00', 'DeviceC', 'normal', NULL, NULL, 0, NULL, NULL, NULL, NULL, '2025-05-06 23:52:22', '2025-05-06 23:52:22');
INSERT INTO `health_monitoring` VALUES (4, 2, '4', '110', 'bpm', '2025-05-08 09:45:00', NULL, 'abnormal', 'medium', 'Heart rate consistently high during monitoring period.', 0, NULL, NULL, NULL, NULL, '2025-05-06 23:52:22', '2025-05-06 23:53:04');

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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_records
-- ----------------------------
INSERT INTO `health_records` VALUES (15, 2, '120/80', 75, 5.60, 36.5, 65.00, 170.00, 22.50, '高血压', '青霉素', '轻度感冒', '2025-05-14 13:34:45', '阿莫西林', '2025-05-14 13:34:45', 2, '初始记录', '2025-04-08 13:01:17', '2025-05-14 13:34:46');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2000 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `menu` VALUES (109, '服务工单管理', 3, 1, 'order', 'services/order', NULL, '', 1, 1, 'C', '1', '0', NULL, 'service', 'admin', '2025-04-05 01:16:24', 'admin', '2025-04-05 01:16:24', '');
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
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知类型',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布 2-已撤回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 0, '社区健康讲座通知', '计划于下月举办老年人健康知识讲座，具体时间待定', NULL, 0, '2023-03-27 09:00:00', '2025-03-28 16:40:54', '2025-03-28 16:41:46');
INSERT INTO `notification` VALUES (2, 0, '端午节活动筹备', '端午节包粽子活动正在筹备中，需要志愿者报名', NULL, 0, '2023-03-27 10:00:00', '2025-03-28 16:41:56', '2023-05-15 14:30:00');
INSERT INTO `notification` VALUES (3, 0, '系统维护通知', '系统将于今晚23:00-24:00进行维护升级，期间暂停服务', NULL, 1, '2023-03-27 11:00:00', '2025-03-28 16:42:07', '2023-05-20 15:00:00');
INSERT INTO `notification` VALUES (4, 0, '老年体检安排', '6月1日-6月5日社区医院为65岁以上老人提供免费体检', NULL, 1, '2023-03-27 11:00:00', '2025-03-28 16:42:13', '2023-05-25 09:00:00');
INSERT INTO `notification` VALUES (5, 0, '防诈骗知识讲座', '5月30日下午2点社区活动中心举办老年人防诈骗讲座', NULL, 1, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-28 10:30:00');
INSERT INTO `notification` VALUES (6, 0, '错误通知示例', '此通知内容有误，已撤回', NULL, 2, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-18 08:00:00');
INSERT INTO `notification` VALUES (7, 0, '活动取消通知', '原定于5月22日的书法班因故取消，时间另行通知', NULL, 2, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-05-21 13:00:00');
INSERT INTO `notification` VALUES (8, 0, '重要：医保政策更新', '2023年最新医保报销政策已更新，请及时查看', NULL, 1, '2023-03-27 10:00:00', '2025-03-28 16:42:03', '2023-06-01 09:00:00');
INSERT INTO `notification` VALUES (9, 2, '服务预约通知', '尊敬的刘大毛用户，您的服务预约（预约号：2）预约已创建，等待审核。预约时间：2025-04-29 08:33:00，服务项目：null。', NULL, 0, '2025-04-05 00:33:49', '2025-04-05 00:33:49', '2025-04-05 00:33:49');
INSERT INTO `notification` VALUES (10, 2, '服务预约通知', '尊敬的刘大毛用户，您的服务预约（预约号：3）预约已创建，等待审核。预约时间：2025-04-29 10:40:00，服务项目：null。', NULL, 0, '2025-04-05 00:39:10', '2025-04-05 00:39:10', '2025-04-05 00:39:10');
INSERT INTO `notification` VALUES (11, 2, '服务预约通知', '尊敬的刘大毛用户，您的服务预约（预约号：4）预约已创建，等待审核。预约时间：2025-04-29 08:54:00，服务项目：null。', NULL, 0, '2025-04-05 00:54:48', '2025-04-05 00:54:48', '2025-04-05 00:54:48');

-- ----------------------------
-- Table structure for physical_exam_report
-- ----------------------------
DROP TABLE IF EXISTS `physical_exam_report`;
CREATE TABLE `physical_exam_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检报告ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID，关联user表',
  `recorder_id` bigint NULL DEFAULT NULL COMMENT '上传人ID，关联user表',
  `date` date NOT NULL COMMENT '体检日期',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '体检医院',
  `main_result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主要结论',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告文件URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_recorder_id`(`recorder_id` ASC) USING BTREE,
  CONSTRAINT `fk_exam_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_exam_recorder` FOREIGN KEY (`recorder_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of physical_exam_report
-- ----------------------------
INSERT INTO `physical_exam_report` VALUES (1, 2, 2, '2024-05-01', '市人民医院', '血脂偏高，建议复查', '/upload/physical_exam/57088d81-7643-4cc0-bc25-6032fa57fcce.pdf', '2025-05-13 17:59:09', '2025-05-13 17:59:09');
INSERT INTO `physical_exam_report` VALUES (2, 2, 2, '2023-11-15', '社区卫生服务中心', '各项指标正常', '/upload/physical_exam/d798d64c-8859-49fb-9477-f483c2163dfd.pdf', '2025-05-13 17:59:09', '2025-05-13 17:59:09');

-- ----------------------------
-- Table structure for physical_exam_report_item
-- ----------------------------
DROP TABLE IF EXISTS `physical_exam_report_item`;
CREATE TABLE `physical_exam_report_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` bigint NOT NULL COMMENT '体检报告ID，关联physical_exam_report表',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标名称',
  `item_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标值',
  `item_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `abnormal_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否异常(0-正常 1-异常)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_report_id`(`report_id` ASC) USING BTREE,
  CONSTRAINT `fk_report_item_report` FOREIGN KEY (`report_id`) REFERENCES `physical_exam_report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检报告指标表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of physical_exam_report_item
-- ----------------------------
INSERT INTO `physical_exam_report_item` VALUES (9, 1, '总胆固醇', '6.2', 'mmol/L', 1);
INSERT INTO `physical_exam_report_item` VALUES (10, 1, '甘油三酯', '1.8', 'mmol/L', 0);
INSERT INTO `physical_exam_report_item` VALUES (11, 1, '高密度脂蛋白', '1.1', 'mmol/L', 0);
INSERT INTO `physical_exam_report_item` VALUES (12, 1, '低密度脂蛋白', '4.0', 'mmol/L', 1);
INSERT INTO `physical_exam_report_item` VALUES (13, 2, '总胆固醇', '4.8', 'mmol/L', 0);
INSERT INTO `physical_exam_report_item` VALUES (14, 2, '甘油三酯', '1.2', 'mmol/L', 0);
INSERT INTO `physical_exam_report_item` VALUES (15, 2, '高密度脂蛋白', '1.3', 'mmol/L', 0);
INSERT INTO `physical_exam_report_item` VALUES (16, 2, '低密度脂蛋白', '2.5', 'mmol/L', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务项目表' ROW_FORMAT = DYNAMIC;

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
  `status` tinyint NOT NULL COMMENT '状态(0-待审核 1-已派单 2-服务中 3-已完成 4-已取消 5-已拒绝)',
  `apply_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预约理由',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `schedule_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `actual_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际费用',
  `actual_duration` int NULL DEFAULT NULL COMMENT '实际服务时长(分钟)',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `service_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务工单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_order
-- ----------------------------
INSERT INTO `service_order` VALUES (3, 2, 1001, 1, '13433432', '1', '2025-04-29 10:40:00', NULL, NULL, NULL, '2025-04-05 00:39:10', NULL, '2025-04-05 00:39:10', NULL);
INSERT INTO `service_order` VALUES (4, 2, 3002, 1, '要求认真仔细一点的阿姨', '1', '2025-04-29 08:54:00', NULL, NULL, NULL, '2025-04-05 00:54:48', NULL, '2025-04-05 00:54:48', NULL);
INSERT INTO `service_order` VALUES (5, 2, 1001, 0, '1111111111111111111', NULL, '2025-05-28 15:11:00', NULL, NULL, 'Ldamao', '2025-05-05 17:12:04', 'Ldamao', '2025-05-05 17:12:04', NULL);

-- ----------------------------
-- Table structure for service_review
-- ----------------------------
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_id` bigint NULL DEFAULT NULL COMMENT '服务项目ID (关联 service_item 表的主键)',
  `order_id` bigint NOT NULL COMMENT '关联订单',
  `elder_id` bigint NOT NULL COMMENT '老人ID (服务接受者, 外键 -> user.user_id)',
  `review_user_id` bigint NOT NULL COMMENT '评价人ID (可能是老人或家属, 外键 -> user.user_id)',
  `review_type` tinyint NOT NULL COMMENT '评价类型 (例如: 0-老人自己评价, 1-家属代评价)',
  `rating` tinyint NOT NULL COMMENT '评分(1-5星)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `review_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `is_anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名 (例如: 0-否, 1-是)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '评价状态 (例如: 0-待审核, 1-已通过, 2-已拒绝)',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '官方回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `admin_reply_user_id` bigint NULL DEFAULT NULL COMMENT '回复的管理员ID (外键 -> user.user_id)',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `createBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志(0-未删除, 1-已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_user`(`elder_id` ASC) USING BTREE,
  INDEX `idx_admin_reply_user_id`(`admin_reply_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_service_review_reply_admin` FOREIGN KEY (`admin_reply_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `service_review_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `service_review_ibfk_2` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_review
-- ----------------------------
INSERT INTO `service_review` VALUES (1, 1001, 3, 2, 0, 0, 5, '服务非常好，医生很专业，态度也很好！强烈推荐！', '2025-05-06 16:30:25', 0, 0, NULL, NULL, NULL, 'Ldamao', 'Ldamao', '首次评价，非常满意', NULL, '2025-05-06 16:30:25', NULL, '2025-05-06 17:27:17', 0);
INSERT INTO `service_review` VALUES (2, 3002, 4, 2, 0, 0, 3, '服务还可以，师傅上门比较准时，但是问题没有完全解决，希望改进。', '2025-05-05 16:30:25', 0, 0, '感谢您的反馈，我们已联系师傅跟进处理，给您带来不便非常抱歉。', '2025-05-06 16:30:25', NULL, 'Ldamao_kin', 'admin', '家属代评，管理员已回复', NULL, '2025-05-05 16:30:25', NULL, '2025-05-06 17:27:17', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '超级管理员', '女', '13800000001', 'bob.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2025-02-26 04:06:56', NULL, '2025-03-27 22:16:52', '');
INSERT INTO `user` VALUES (2, 'Ldamao', '123456', '刘大毛', '女', '13800000002', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '340881199005204216', '1990-05-20', 35, '张伟', '13812345678', '中等，需定期监测血压、血糖，并注意饮食控制。', 'staff', '2025-03-22 19:46:06', NULL, '2025-03-28 15:22:26', '');
INSERT INTO `user` VALUES (3, 'Lxiaomao', '123456', '刘小毛', '男', '13800000004', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '', NULL, NULL, '', '', '', 'staff', '2025-03-27 15:38:04', NULL, '2025-03-28 15:22:21', '');
INSERT INTO `user` VALUES (25, 'Lshumei', '123456', '刘淑梅', '未知', '15349545835', 'yopsu@mailto.plus', NULL, NULL, 1, '', '', '12345619900101123X', '1990-01-01', 35, '王小明', '13987654321  ', '慢性咳嗽', NULL, NULL, NULL, NULL, '');
INSERT INTO `user` VALUES (26, 'Wdaniu', '123456', '王大牛', '未知', '13458684687', '23468746@qq.com', NULL, NULL, 1, '', '', '', NULL, NULL, '', '', '', 'admin', '2025-04-06 14:59:16', 'admin', '2025-04-06 14:59:16', '');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 4);
INSERT INTO `user_role` VALUES (2, 1);
INSERT INTO `user_role` VALUES (3, 2);
INSERT INTO `user_role` VALUES (25, 1);
INSERT INTO `user_role` VALUES (26, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预警记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of warning_record
-- ----------------------------

-- ----------------------------
-- Triggers structure for table activity_check_in
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_activity_check_in_after_insert`;
delimiter ;;
CREATE TRIGGER `trg_activity_check_in_after_insert` AFTER INSERT ON `activity_check_in` FOR EACH ROW BEGIN
  UPDATE `activity_register` SET `status` = 4 WHERE `id` = NEW.register_id;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table activity_check_in
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_activity_check_in_after_delete`;
delimiter ;;
CREATE TRIGGER `trg_activity_check_in_after_delete` AFTER DELETE ON `activity_check_in` FOR EACH ROW BEGIN
  UPDATE `activity_register` SET `status` = 1 WHERE `id` = OLD.register_id;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
