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

 Date: 19/05/2025 10:55:19
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
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (1, '社区烧烤活动', '7', '欢迎大家参加本次社区烧烤活动，一起享受美食、聊天、玩游戏。', '/images/activities/default.jpg', '2023-05-01 18:00:00', '2023-05-01 21:00:00', '2023-04-24 18:00:00', '2023-04-30 18:00:00', '社区广场', 100, 5, 1, '健康,娱乐,社交', 1, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (2, '社区健康讲座', '2', '本次讲座将邀请专业医生为大家讲解如何保持健康的生活方式。', '/images/activities/default.jpg', '2023-05-10 19:30:00', '2023-05-10 21:00:00', '2023-05-03 19:30:00', '2023-05-09 19:30:00', '社区活动中心', 50, 5, 1, '健康,娱乐,社交', 2, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (3, '社区植树活动', '4', '让我们一起为社区种下更多绿色植被,共建美丽家园。', '/images/activities/default.jpg', '2023-05-15 09:00:00', '2023-05-15 12:00:00', '2023-05-08 09:00:00', '2023-05-14 09:00:00', '社区公园', 80, 5, 1, '健康,娱乐,社交', 3, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (4, '社区亲子游戏日', '9', '孩子们快来参加这次有趣的亲子游戏活动吧!', '/images/activities/default.jpg', '2023-05-20 14:00:00', '2023-05-20 17:00:00', '2023-05-13 14:00:00', '2023-05-19 14:00:00', '社区活动中心', 60, 5, 1, '健康,娱乐,社交', 4, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (5, '社区篮球赛', '3', '欢迎大家参加本次社区篮球赛,展现你的球技!', '/images/activities/default.jpg', '2023-05-25 19:00:00', '2023-05-25 21:30:00', '2023-05-18 19:00:00', '2023-05-24 19:00:00', '社区体育馆', 40, 5, 1, '健康,娱乐,社交', 5, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (6, '社区书籍交换会', '9', '把你家里的旧书带来,和其他人交换心仪的新书吧。', '/images/activities/default.jpg', '2023-06-01 14:00:00', '2023-06-01 17:00:00', '2023-05-25 14:00:00', '2023-05-31 14:00:00', '社区图书馆', 30, 5, 1, '健康,娱乐,社交', 6, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (7, '社区环保讲座', '8', '让我们一起学习如何在日常生活中实践环保理念。', '/images/activities/default.jpg', '2023-06-05 19:30:00', '2023-06-05 21:00:00', '2023-05-29 19:30:00', '2023-06-04 19:30:00', '社区活动中心', 50, 5, 1, '健康,娱乐,社交', 7, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (8, '社区DIY手工坊', '9', '来学习制作各种有趣的手工艺品吧!', '/images/activities/default.jpg', '2023-06-10 14:00:00', '2023-06-10 17:00:00', '2023-06-03 14:00:00', '2023-06-09 14:00:00', '社区活动中心', 40, 5, 1, '健康,娱乐,社交', 8, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (9, '社区烹饪课', '9', '邀请专业厨师为大家讲解美味佳肴的制作方法。', '/images/activities/default.jpg', '2023-06-15 19:00:00', '2023-06-15 21:30:00', '2023-06-08 19:00:00', '2023-06-14 19:00:00', '社区活动中心', 30, 5, 1, '健康,娱乐,社交', 9, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (10, '社区户外拓展', '9', '一起来体验户外拓展活动,增强团队合作能力。', '/images/activities/default.jpg', '2023-06-20 09:00:00', '2023-06-20 16:00:00', '2023-06-13 09:00:00', '2023-06-19 09:00:00', '社区公园', 60, 5, 1, '健康,娱乐,社交', 10, 1, '2025-03-13 18:16:28', '2025-05-15 17:33:58');
INSERT INTO `activities` VALUES (11, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', '/images/activities/default.jpg', '2023-06-25 19:30:00', '2023-06-25 21:30:00', '2023-06-18 19:30:00', '2023-06-24 19:30:00', '社区文化中心', 80, NULL, 1, '???,???,???', 1, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (12, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', '/images/activities/default.jpg', '2023-07-01 14:00:00', '2023-07-01 17:00:00', '2023-06-24 14:00:00', '2023-06-30 14:00:00', '社区文化中心', 50, NULL, 1, '???,???,???', 2, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (13, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', '/images/activities/default.jpg', '2023-07-05 19:00:00', '2023-07-05 21:00:00', '2023-06-28 19:00:00', '2023-07-04 19:00:00', '社区活动中心', 40, NULL, 1, '???,???,???', 3, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (14, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', '/images/activities/default.jpg', '2023-07-10 09:00:00', '2023-07-10 12:00:00', '2023-07-03 09:00:00', '2023-07-09 09:00:00', '社区体育场', 80, NULL, 1, '???,???,???', 4, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (15, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', '/images/activities/default.jpg', '2023-07-15 19:00:00', '2023-07-15 21:30:00', '2023-07-08 19:00:00', '2023-07-14 19:00:00', '社区文化中心', 60, NULL, 1, '???,???,???', 5, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (16, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', '/images/activities/default.jpg', '2023-07-20 14:00:00', '2023-07-20 17:00:00', '2023-07-13 14:00:00', '2023-07-19 14:00:00', '社区公园', 30, NULL, 1, '???,???,???', 6, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (17, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', '/images/activities/default.jpg', '2023-07-25 19:30:00', '2023-07-25 21:00:00', '2023-07-18 19:30:00', '2023-07-24 19:30:00', '社区图书馆', 20, NULL, 1, '???,???,???', 7, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (18, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', '/images/activities/default.jpg', '2023-08-01 14:00:00', '2023-08-01 17:00:00', '2023-07-25 14:00:00', '2023-07-31 14:00:00', '社区文化中心', 50, NULL, 1, '???,???,???', 8, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (19, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', '/images/activities/default.jpg', '2023-08-05 19:00:00', '2023-08-05 21:30:00', '2023-07-29 19:00:00', '2023-08-04 19:00:00', '社区活动中心', 40, NULL, 1, '???,???,???', 9, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (20, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', '/images/activities/default.jpg', '2023-08-10 09:00:00', '2023-08-10 16:00:00', '2023-08-03 09:00:00', '2023-08-09 09:00:00', '社区公园', 60, NULL, 1, '???,???,???', 10, 1, '2025-03-13 18:17:14', '2025-05-15 17:33:11');
INSERT INTO `activities` VALUES (21, '社区音乐会', '1', '欣赏各种精彩的音乐表演,享受美好的音乐时光。', '/images/activities/default.jpg', '2023-06-25 19:30:00', '2023-06-25 21:30:00', '2023-06-18 19:30:00', '2023-06-24 19:30:00', '社区文化中心', 80, NULL, 1, '健康,娱乐,社交', 1, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (22, '社区绘画展', '1', '欣赏社区居民的绘画作品,感受艺术的魅力。', '/images/activities/default.jpg', '2023-07-01 14:00:00', '2023-07-01 17:00:00', '2023-06-24 14:00:00', '2023-06-30 14:00:00', '社区文化中心', 50, NULL, 1, '健康,娱乐,社交', 2, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (23, '社区舞蹈课', '3', '学习各种流行舞蹈,展现自己的舞蹈才能。', '/images/activities/default.jpg', '2023-07-05 19:00:00', '2023-07-05 21:00:00', '2023-06-28 19:00:00', '2023-07-04 19:00:00', '社区活动中心', 40, NULL, 1, '健康,娱乐,社交', 3, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (24, '社区趣味运动会', '3', '参加各种有趣的运动项目,增强身体素质。', '/images/activities/default.jpg', '2023-07-10 09:00:00', '2023-07-10 12:00:00', '2023-07-03 09:00:00', '2023-07-09 09:00:00', '社区体育场', 80, NULL, 1, '健康,娱乐,社交', 4, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (25, '社区电影欣赏', '1', '观看精彩的电影作品,享受电影带来的视听盛宴。', '/images/activities/default.jpg', '2023-07-15 19:00:00', '2023-07-15 21:30:00', '2023-07-08 19:00:00', '2023-07-14 19:00:00', '社区文化中心', 60, NULL, 1, '健康,娱乐,社交', 5, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (26, '社区园艺培训', '6', '学习园艺知识,种植自己喜欢的花草植物。', '/images/activities/default.jpg', '2023-07-20 14:00:00', '2023-07-20 17:00:00', '2023-07-13 14:00:00', '2023-07-19 14:00:00', '社区公园', 30, NULL, 1, '健康,娱乐,社交', 6, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (27, '社区读书会', '9', '一起分享读书心得,探讨有趣的文学作品。', '/images/activities/default.jpg', '2023-07-25 19:30:00', '2023-07-25 21:00:00', '2023-07-18 19:30:00', '2023-07-24 19:30:00', '社区图书馆', 20, NULL, 1, '健康,娱乐,社交', 7, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (28, '社区摄影展', '9', '展示社区居民的摄影作品,欣赏生活中的美好瞬间。', '/images/activities/default.jpg', '2023-08-01 14:00:00', '2023-08-01 17:00:00', '2023-07-25 14:00:00', '2023-07-31 14:00:00', '社区文化中心', 50, NULL, 1, '健康,娱乐,社交', 8, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (29, '社区烹饪大赛', '9', '展示大家的厨艺,品尝各种美味佳肴。', '/images/activities/default.jpg', '2023-08-05 19:00:00', '2023-08-05 21:30:00', '2023-07-29 19:00:00', '2023-08-04 19:00:00', '社区活动中心', 40, NULL, 1, '健康,娱乐,社交', 9, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');
INSERT INTO `activities` VALUES (30, '社区户外探险', '9', '一起探索大自然,体验户外运动的乐趣。', '/images/activities/default.jpg', '2023-08-10 09:00:00', '2023-08-10 16:00:00', '2023-08-03 09:00:00', '2023-08-09 09:00:00', '社区公园', 60, NULL, 1, '健康,娱乐,社交', 10, 1, '2025-03-13 18:17:30', '2025-05-15 17:33:12');

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
INSERT INTO `activity_register` VALUES (15, 22, 2, 2, 0, '2025-05-17 16:53:56', 0, NULL, '2025-05-12 12:41:23', '2025-05-12 12:41:23', 0);
INSERT INTO `activity_register` VALUES (16, 23, 2, 2, 0, '2025-05-17 16:54:11', 0, NULL, '2025-05-12 19:23:01', '2025-05-12 19:23:01', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 199 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `dict_data` VALUES (145, 1, '一般检查', '一般检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '体重、血压等一般体检项目');
INSERT INTO `dict_data` VALUES (146, 2, '抽血检查', '抽血检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '血常规、生化等抽血检查项目');
INSERT INTO `dict_data` VALUES (147, 3, 'X光检查', 'X光检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, 'X光影像检查项目');
INSERT INTO `dict_data` VALUES (148, 4, 'B超检查', 'B超检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '超声检查项目');
INSERT INTO `dict_data` VALUES (149, 5, '特殊检查', '特殊检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '特殊检查项目');
INSERT INTO `dict_data` VALUES (150, 6, '妇科检查', '妇科检查', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '妇科相关检查项目');
INSERT INTO `dict_data` VALUES (151, 7, '其他', '其他', 'physical_exam_category', NULL, NULL, 'N', '0', '', NULL, '', NULL, '其他未分类检查项目');
INSERT INTO `dict_data` VALUES (152, 1, '血脂检查', '血脂检查', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '血脂相关检查项目');
INSERT INTO `dict_data` VALUES (153, 1, '血脂检查', '血脂检查', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '血脂相关检查项目');
INSERT INTO `dict_data` VALUES (154, 2, '血常规', '血常规', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '血液常规检查项目');
INSERT INTO `dict_data` VALUES (155, 3, '肝功能', '肝功能', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '肝功能检查项目');
INSERT INTO `dict_data` VALUES (156, 4, '肾功能', '肾功能', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '肾功能检查项目');
INSERT INTO `dict_data` VALUES (157, 5, '尿常规', '尿常规', 'physical_exam_subcategory', NULL, NULL, 'N', '0', '', NULL, '', NULL, '尿液常规检查项目');
INSERT INTO `dict_data` VALUES (158, 1, '系统通知', 'system', 'notice_type', '', 'primary', 'Y', '0', 'admin', '2025-05-15 21:40:18', 'admin', '2025-05-15 21:40:18', '系统自动发送的通知');
INSERT INTO `dict_data` VALUES (159, 2, '活动通知', 'activity', 'notice_type', '', 'success', 'N', '0', 'admin', '2025-05-15 21:40:18', 'admin', '2025-05-15 21:40:18', '与活动相关的通知');
INSERT INTO `dict_data` VALUES (160, 3, '服务通知', 'service', 'notice_type', '', 'info', 'N', '0', 'admin', '2025-05-15 21:40:18', 'admin', '2025-05-15 21:40:18', '与服务相关的通知');
INSERT INTO `dict_data` VALUES (161, 4, '公告', 'announcement', 'notice_type', '', 'warning', 'N', '0', 'admin', '2025-05-15 21:40:18', 'admin', '2025-05-15 21:40:18', '系统公告');
INSERT INTO `dict_data` VALUES (162, 5, '紧急通知', 'urgent', 'notice_type', '', 'danger', 'N', '0', 'admin', '2025-05-15 21:40:18', 'admin', '2025-05-15 21:40:18', '紧急事项通知');
INSERT INTO `dict_data` VALUES (168, 1, '草稿', '0', 'notice_status', '', 'info', 'Y', '0', 'admin', '2025-05-15 21:53:25', '', NULL, '通知草稿，尚未发布');
INSERT INTO `dict_data` VALUES (169, 2, '已发布', '1', 'notice_status', '', 'success', 'N', '0', 'admin', '2025-05-15 21:53:25', '', NULL, '通知已发布，正常显示');
INSERT INTO `dict_data` VALUES (170, 3, '已关闭', '2', 'notice_status', '', 'danger', 'N', '0', 'admin', '2025-05-15 21:53:25', '', NULL, '通知已关闭，不再显示');
INSERT INTO `dict_data` VALUES (171, 4, '已过期', '3', 'notice_status', '', 'warning', 'N', '0', 'admin', '2025-05-15 21:53:25', '', NULL, '通知已过期，自动不再显示');
INSERT INTO `dict_data` VALUES (172, 5, '已归档', '4', 'notice_status', '', 'info', 'N', '0', 'admin', '2025-05-15 21:53:25', '', NULL, '通知已归档，仅可查看历史');
INSERT INTO `dict_data` VALUES (174, 1, '初始记录', '初始记录', 'health_record_type', '', 'default', 'Y', '0', 'admin', '2025-05-18 23:52:00', '', NULL, '健康档案初始记录');
INSERT INTO `dict_data` VALUES (175, 2, '定期检查', '定期检查', 'health_record_type', '', 'default', 'N', '0', 'admin', '2025-05-18 23:52:00', '', NULL, '健康档案定期检查');
INSERT INTO `dict_data` VALUES (176, 3, '随访记录', '随访记录', 'health_record_type', '', 'default', 'N', '0', 'admin', '2025-05-18 23:52:00', '', NULL, '健康档案随访记录');
INSERT INTO `dict_data` VALUES (177, 4, '紧急记录', '紧急记录', 'health_record_type', '', 'warning', 'N', '0', 'admin', '2025-05-18 23:52:00', '', NULL, '健康档案紧急记录');
INSERT INTO `dict_data` VALUES (178, 1, '偏瘦', '18.5', 'bmi_status', 'text-warning', 'warning', 'N', '0', 'admin', '2025-05-18 23:55:52', '', NULL, 'BMI < 18.5');
INSERT INTO `dict_data` VALUES (179, 2, '正常', '24', 'bmi_status', 'text-success', 'success', 'Y', '0', 'admin', '2025-05-18 23:55:52', '', NULL, '18.5 ≤ BMI < 24');
INSERT INTO `dict_data` VALUES (180, 3, '超重', '28', 'bmi_status', 'text-warning', 'warning', 'N', '0', 'admin', '2025-05-18 23:55:52', '', NULL, '24 ≤ BMI < 28');
INSERT INTO `dict_data` VALUES (181, 4, '肥胖', '100', 'bmi_status', 'text-danger', 'danger', 'N', '0', 'admin', '2025-05-18 23:55:52', '', NULL, 'BMI ≥ 28');
INSERT INTO `dict_data` VALUES (182, 1, '已处理', 'true', 'process_status', '', 'success', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '已处理状态');
INSERT INTO `dict_data` VALUES (183, 2, '未处理', 'false', 'process_status', '', 'info', 'Y', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '未处理状态');
INSERT INTO `dict_data` VALUES (184, 1, '血压', '1', 'monitor_type', '', 'primary', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '血压监测');
INSERT INTO `dict_data` VALUES (185, 2, '血糖', '2', 'monitor_type', '', 'primary', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '血糖监测');
INSERT INTO `dict_data` VALUES (186, 3, '心率', '3', 'monitor_type', '', 'primary', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '心率监测');
INSERT INTO `dict_data` VALUES (187, 4, '体温', '4', 'monitor_type', '', 'primary', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '体温监测');
INSERT INTO `dict_data` VALUES (188, 5, '体重', '5', 'monitor_type', '', 'primary', 'Y', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '体重监测');
INSERT INTO `dict_data` VALUES (189, 1, '正常', 'normal', 'monitor_status', '', 'success', 'Y', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '正常状态');
INSERT INTO `dict_data` VALUES (190, 2, '异常', 'abnormal', 'monitor_status', '', 'danger', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '异常状态');
INSERT INTO `dict_data` VALUES (191, 1, '轻度', 'low', 'abnormal_level', '', 'info', 'Y', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '轻度异常');
INSERT INTO `dict_data` VALUES (192, 2, '中度', 'medium', 'abnormal_level', '', 'warning', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '中度异常');
INSERT INTO `dict_data` VALUES (193, 3, '重度', 'high', 'abnormal_level', '', 'danger', 'N', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '重度异常');
INSERT INTO `dict_data` VALUES (194, 1, '正常', '1', 'service_status', '', 'primary', 'Y', '0', 'admin', '2025-05-19 00:17:14', 'admin', '2025-05-19 00:17:14', '服务项目正常状态（上架）');
INSERT INTO `dict_data` VALUES (195, 2, '停用', '0', 'service_status', '', 'danger', 'N', '0', 'admin', '2025-05-19 00:17:14', 'admin', '2025-05-19 00:17:14', '服务项目停用状态（下架）');
INSERT INTO `dict_data` VALUES (196, 1, '待审核', '0', 'service_review_status', '', 'info', 'Y', '0', 'admin', '2025-05-19 09:26:56', '', NULL, '服务评价待管理员审核状态');
INSERT INTO `dict_data` VALUES (197, 2, '已通过', '1', 'service_review_status', '', 'success', 'N', '0', 'admin', '2025-05-19 09:26:56', '', NULL, '服务评价审核通过状态');
INSERT INTO `dict_data` VALUES (198, 3, '已拒绝', '2', 'service_review_status', '', 'danger', 'N', '0', 'admin', '2025-05-19 09:26:56', '', NULL, '服务评价审核拒绝状态');

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
) ENGINE = InnoDB AUTO_INCREMENT = 123 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES (1, '用户性别', 'user_sex', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '用户性别列表');
INSERT INTO `dict_type` VALUES (2, '菜单状态', 'show_hide', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '菜单状态列表');
INSERT INTO `dict_type` VALUES (3, '系统开关', 'normal_disable', '0', 'admin', '2025-05-14 17:23:37', 'admin', '2025-05-14 17:23:37', '系统开关列表');
INSERT INTO `dict_type` VALUES (4, '系统状态', 'yes_no', '0', 'admin', '2025-05-15 17:48:07', 'admin', '2025-05-15 17:48:07', '系统是否列表');
INSERT INTO `dict_type` VALUES (5, '通知类型', 'notice_type', '0', 'admin', '2025-05-15 17:48:07', 'admin', '2025-05-15 17:48:07', '通知类型列表');
INSERT INTO `dict_type` VALUES (6, '通知状态', 'notice_status', '0', 'admin', '2025-05-15 17:48:08', 'admin', '2025-05-15 17:48:08', '通知状态列表');
INSERT INTO `dict_type` VALUES (7, '操作类型', 'oper_type', '0', 'admin', '2025-05-15 17:48:09', 'admin', '2025-05-15 17:48:09', '操作类型列表');
INSERT INTO `dict_type` VALUES (8, '系统状态', 'common_status', '0', 'admin', '2025-05-15 17:48:10', 'admin', '2025-05-15 17:48:10', '登录状态列表');
INSERT INTO `dict_type` VALUES (9, '服务类型', 'service_type', '0', 'admin', '2025-05-15 17:48:10', 'admin', '2025-05-15 17:48:10', '服务项目的类型');
INSERT INTO `dict_type` VALUES (10, '活动类型', 'activity_type', '0', 'admin', '2025-05-15 17:48:12', 'admin', '2025-05-15 17:48:12', '活动类型列表');
INSERT INTO `dict_type` VALUES (11, '工单状态', 'order_status', '0', 'admin', '2025-05-15 17:48:13', 'admin', '2025-05-15 17:48:13', '服务工单的状态');
INSERT INTO `dict_type` VALUES (12, '家庭关系类型', 'kinship_type', '0', 'admin', '2025-05-15 17:48:14', 'admin', '2025-05-15 17:48:14', '老人和家属的关系类型');
INSERT INTO `dict_type` VALUES (13, '健康等级', 'health_level', '0', 'admin', '2025-05-15 17:48:15', 'admin', '2025-05-15 17:48:15', '老人健康状况分级');
INSERT INTO `dict_type` VALUES (14, '服务评价等级', 'service_rating', '0', 'admin', '2025-05-15 17:48:16', 'admin', '2025-05-15 17:48:16', '服务评价星级');
INSERT INTO `dict_type` VALUES (15, '老人风险等级', 'elder_risk_level', '0', 'admin', '2025-05-15 17:48:17', 'admin', '2025-05-15 17:48:17', '老人健康风险分级');
INSERT INTO `dict_type` VALUES (16, '证件类型', 'id_type', '0', 'admin', '2025-05-15 17:48:18', 'admin', '2025-05-15 17:48:18', '用户证件类型');
INSERT INTO `dict_type` VALUES (17, '活动报名状态', 'activity_register_status', '0', 'admin', '2025-05-15 17:48:19', 'admin', '2025-05-15 17:48:19', '活动报名审核状态');
INSERT INTO `dict_type` VALUES (18, '体检报告分类', 'physical_exam_category', '0', '', '2025-05-15 17:48:20', '', '2025-05-15 17:48:20', '体检报告中的检查项目分类');
INSERT INTO `dict_type` VALUES (19, '体检报告子类型', 'physical_exam_subcategory', '0', '', '2025-05-15 17:48:23', '', '2025-05-15 17:48:23', '体检报告中的检查项目子类型');
INSERT INTO `dict_type` VALUES (112, '健康记录类型', 'health_record_type', '0', 'admin', '2025-05-18 23:52:00', '', NULL, '健康档案记录类型');
INSERT INTO `dict_type` VALUES (113, 'BMI状态', 'bmi_status', '0', 'admin', '2025-05-18 23:55:52', '', NULL, 'BMI指数状态分类');
INSERT INTO `dict_type` VALUES (116, '处理状态', 'process_status', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '健康监测处理状态');
INSERT INTO `dict_type` VALUES (117, '监测类型', 'monitor_type', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '健康监测类型');
INSERT INTO `dict_type` VALUES (118, '监测状态', 'monitor_status', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '健康监测状态');
INSERT INTO `dict_type` VALUES (119, '异常级别', 'abnormal_level', '0', 'admin', '2025-05-19 00:00:49', '', NULL, '健康监测异常级别');
INSERT INTO `dict_type` VALUES (120, '服务状态', 'service_status', '0', 'admin', '2025-05-19 00:17:14', 'admin', '2025-05-19 00:17:14', '服务项目状态：上架或下架');
INSERT INTO `dict_type` VALUES (122, '服务评价状态', 'service_review_status', '0', 'admin', '2025-05-19 09:26:56', 'admin', '2025-05-19 09:26:56', '服务评价的审核状态');

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
INSERT INTO `elder_kin_relation` VALUES (1000, 1001, '父女');
INSERT INTO `elder_kin_relation` VALUES (1002, 1003, '子女');

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
  INDEX `idx_related_data_id`(`related_data_id` ASC) USING BTREE,
  INDEX `fk_health_alert_handler`(`handler_id` ASC) USING BTREE,
  CONSTRAINT `fk_health_alert_elder` FOREIGN KEY (`elder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_health_alert_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_health_alert_monitor` FOREIGN KEY (`related_data_id`) REFERENCES `health_monitoring` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康预警表' ROW_FORMAT = Dynamic;

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
INSERT INTO `health_monitoring` VALUES (1, 2, '1', '125/78', 'mmHg', '2025-05-07 09:00:00', 'DEV00001', 'normal', NULL, NULL, 1, NULL, NULL, NULL, 15, '2025-05-06 21:18:04', '2025-05-17 13:41:12');
INSERT INTO `health_monitoring` VALUES (2, 2, '3', '36.8', '°C', '2025-05-07 11:00:00', 'DEV00002', 'normal', NULL, NULL, 0, NULL, NULL, NULL, 15, '2025-05-06 21:18:04', '2025-05-15 17:34:27');
INSERT INTO `health_monitoring` VALUES (3, 2, '2', '6.5', 'mmol/L', '2025-05-08 08:30:00', 'DeviceC', 'normal', NULL, NULL, 0, NULL, NULL, NULL, 15, '2025-05-06 23:52:22', '2025-05-15 17:34:27');
INSERT INTO `health_monitoring` VALUES (4, 2, '4', '110', 'bpm', '2025-05-08 09:45:00', 'DEV00004', 'abnormal', 'medium', 'Heart rate consistently high during monitoring period.', 0, NULL, NULL, NULL, 15, '2025-05-06 23:52:22', '2025-05-15 17:34:27');

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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_records
-- ----------------------------
INSERT INTO `health_records` VALUES (15, 2, '120/81', 75, 5.60, 36.5, 65.00, 170.00, 22.50, '高血压1', '青霉素', '轻度感冒2', '2025-05-17 01:26:56', '阿莫西林', '2025-05-17 01:26:56', 2, '初始记录', '2025-04-08 13:01:17', '2025-05-17 13:30:24');
INSERT INTO `health_records` VALUES (16, 1002, '120/80', 60, 4.00, 36.5, 60.00, 168.00, 21.30, '1', '2', '1', NULL, '', '2025-05-18 23:40:35', 999, '初始记录', '2025-05-18 23:41:05', '2025-05-18 23:41:34');

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '系统管理中心', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'Setting', 'admin', '2025-05-15 18:31:50', '', '2025-05-15 18:31:50', '');
INSERT INTO `menu` VALUES (3, '服务管理中心', 0, 2, 'services', NULL, '', '', 1, 0, 'M', '0', '0', '', 'Service', 'admin', '2025-05-15 18:31:50', '', '2025-05-15 18:31:50', '');
INSERT INTO `menu` VALUES (4, '健康管理中心', 0, 3, 'health', NULL, '', '', 1, 1, 'M', '0', '0', '', 'Checked', 'admin', '2025-05-15 18:31:50', '', '2025-05-15 18:31:50', '');
INSERT INTO `menu` VALUES (5, '社区活动中心', 0, 4, 'activity', NULL, '', '', 1, 0, 'M', '0', '0', '', 'Place', 'admin', '2025-05-15 18:31:50', '', '2025-05-15 18:31:50', '');
INSERT INTO `menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 'UserManagement', 1, 0, 'C', '1', '0', 'system:user:list', 'user', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 'RoleManagement', 1, 0, 'C', '1', '0', 'system:role:list', 'peoples', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 'MenuManagement', 1, 0, 'C', '1', '0', 'system:menu:list', 'menu', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (103, '字典管理', 1, 4, 'dict', 'system/dict/index', '', 'DictManagement', 1, 0, 'C', '1', '0', 'system:dict:list', 'dict', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (104, '字典数据', 1, 5, 'dict/data/type/:dictType', 'system/dict/data', '', 'DictDataManagement', 1, 1, 'C', '0', '0', 'system:dict:data', 'Edit', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (105, '系统设置', 1, 6, 'setting', 'system/SystemSetting', '', 'SystemSetting', 1, 0, 'C', '0', '0', 'system:config:list', 'Setting', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (106, '个人中心', 1, 7, 'user/profile', 'system/user/profile/index', '', 'UserProfile', 1, 1, 'C', '0', '0', 'system:user:profile', 'Avatar', 'admin', '2025-05-15 18:32:23', '', '2025-05-15 18:32:23', '');
INSERT INTO `menu` VALUES (300, '服务项目管理', 3, 1, 'service-items', 'services/items/index', '', 'ServiceItems', 1, 1, 'C', '0', '0', 'services:items:list', 'Service', 'admin', '2025-05-15 18:33:18', '', '2025-05-15 18:33:18', '');
INSERT INTO `menu` VALUES (301, '服务工单管理', 3, 1, 'appointments', 'services/order/index', '', 'Appointments', 1, 1, 'C', '0', '0', 'services:appointments:list', 'Service', 'admin', '2025-05-15 18:33:18', '', '2025-05-15 18:33:18', '');
INSERT INTO `menu` VALUES (302, '服务评价管理', 3, 3, 'reviews', 'services/reviews/index', '', 'ServiceReviews', 1, 0, 'C', '0', '0', 'services:reviews:list', 'Star', 'admin', '2025-05-15 18:33:18', '', '2025-05-15 18:33:18', '');
INSERT INTO `menu` VALUES (1001, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:query', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1002, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:add', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1003, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:edit', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1004, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:remove', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1005, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:export', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1006, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:import', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1007, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:resetPwd', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1008, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:query', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1009, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:add', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1010, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:edit', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1011, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:remove', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1012, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:export', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1013, '角色授权', 101, 6, '', '', '', '', 1, 0, 'F', '1', '0', 'system:role:authorize', '#', 'admin', '2025-05-15 18:47:08', '', '2025-05-15 18:47:08', '');
INSERT INTO `menu` VALUES (1014, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:menu:query', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1015, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:menu:add', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1016, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:menu:edit', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1017, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:menu:remove', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1018, '字典查询', 103, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:query', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1019, '字典新增', 103, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:add', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1020, '字典修改', 103, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:edit', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1021, '字典删除', 103, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:remove', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1022, '字典导出', 103, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:export', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1023, '字典数据查询', 104, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:data:query', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1024, '字典数据新增', 104, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:data:add', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1025, '字典数据修改', 104, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:data:edit', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1026, '字典数据删除', 104, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:data:remove', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1027, '字典数据导出', 104, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'system:dict:data:export', '#', 'admin', '2025-05-15 18:48:11', '', '2025-05-15 18:48:11', '');
INSERT INTO `menu` VALUES (1028, '参数查询', 105, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:config:query', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1029, '参数新增', 105, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:config:add', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1030, '参数修改', 105, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:config:edit', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1031, '参数删除', 105, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:config:remove', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1032, '参数导出', 105, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'system:config:export', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1033, '个人资料', 106, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:profile:view', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1034, '修改资料', 106, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:profile:update', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1035, '修改密码', 106, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:profile:resetPwd', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (1036, '头像上传', 106, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'system:user:profile:avatar', '#', 'admin', '2025-05-15 18:49:30', '', '2025-05-15 18:49:30', '');
INSERT INTO `menu` VALUES (3100, '项目查询', 300, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'services:items:query', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3101, '项目新增', 300, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'services:items:add', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3102, '项目修改', 300, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'services:items:edit', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3103, '项目删除', 300, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'services:items:remove', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3104, '项目导出', 300, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'services:items:export', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3105, '预约查询', 301, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:query', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3106, '预约新增', 301, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:add', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3107, '预约修改', 301, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:edit', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3108, '预约删除', 301, 4, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:remove', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3109, '预约导出', 301, 5, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:export', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3110, '预约审核', 301, 6, '', '', '', '', 1, 0, 'F', '1', '0', 'services:appointments:approve', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3111, '评价查询', 302, 1, '', '', '', '', 1, 0, 'F', '1', '0', 'services:reviews:query', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3112, '评价删除', 302, 2, '', '', '', '', 1, 0, 'F', '1', '0', 'services:reviews:remove', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3113, '评价导出', 302, 3, '', '', '', '', 1, 0, 'F', '1', '0', 'services:reviews:export', '#', 'admin', '2025-05-15 18:50:30', '', '2025-05-15 18:50:30', '');
INSERT INTO `menu` VALUES (3120, '通知公告管理', 0, 5, '/notice', '', '', '', 1, 0, 'M', '0', '0', '', 'Notification', 'admin', '2025-05-18 18:58:47', 'admin', '2025-05-18 18:58:47', '');
INSERT INTO `menu` VALUES (3121, '首页', 0, 0, 'home', '', '', '', 1, 0, 'M', '0', '0', '', 'HomeFilled', 'admin', '2025-05-18 19:03:57', 'admin', '2025-05-18 19:03:57', '');

-- ----------------------------
-- Table structure for menu_backup
-- ----------------------------
DROP TABLE IF EXISTS `menu_backup`;
CREATE TABLE `menu_backup`  (
  `menu_id` bigint NOT NULL DEFAULT 0 COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu_backup
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 0, '社区健康讲座通知', '计划于下月举办老年人健康知识讲座，具体时间待定', '系统通知', 0, '2023-03-27 09:00:00', '2025-05-15 22:04:02', '2025-03-28 16:41:46');
INSERT INTO `notification` VALUES (2, 0, '端午节活动筹备', '端午节包粽子活动正在筹备中，需要志愿者报名', '紧急通知', 0, '2023-03-27 10:00:00', '2025-05-15 21:26:31', '2023-05-15 14:30:00');
INSERT INTO `notification` VALUES (3, 0, '系统维护通知', '系统将于今晚23:00-24:00进行维护升级，期间暂停服务', '紧急通知', 1, '2023-03-27 11:00:00', '2025-05-15 22:00:14', '2023-05-20 15:00:00');
INSERT INTO `notification` VALUES (5, 0, '防诈骗知识讲座', '5月30日下午2点社区活动中心举办老年人防诈骗讲座', '系统通知', 1, '2023-03-27 10:00:00', '2025-05-15 22:04:09', '2023-05-28 10:30:00');
INSERT INTO `notification` VALUES (7, 0, '活动取消通知', '原定于5月22日的书法班因故取消，时间另行通知', '系统通知', 2, '2023-03-27 10:00:00', '2025-05-15 22:04:10', '2023-05-21 13:00:00');
INSERT INTO `notification` VALUES (8, 0, '重要：医保政策更新', '2023年最新医保报销政策已更新，请及时查看', '工作通知', 1, '2023-03-27 10:00:00', '2025-05-15 21:29:15', '2023-06-01 09:00:00');
INSERT INTO `notification` VALUES (16, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：6）预约已创建，等待审核。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 08:42:00\n\n您的预约正在审核中，请耐心等待。审核结果将以系统通知的形式告知您。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:42:28', '2025-05-17 01:42:28', '2025-05-17 01:42:28');
INSERT INTO `notification` VALUES (17, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：6）预约审核通过。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 08:42:00\n\n您的预约已通过审核，服务人员将按预约时间上门服务，请保持电话畅通。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:44:08', '2025-05-17 01:44:08', '2025-05-17 01:44:08');
INSERT INTO `notification` VALUES (18, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：6）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 08:42:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:53:19', '2025-05-17 01:53:19', '2025-05-17 01:53:19');
INSERT INTO `notification` VALUES (19, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：5）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-28 15:11:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:55:14', '2025-05-17 01:55:14', '2025-05-17 01:55:14');
INSERT INTO `notification` VALUES (20, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：4）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-04-29 08:54:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:55:17', '2025-05-17 01:55:17', '2025-05-17 01:55:17');
INSERT INTO `notification` VALUES (21, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：3）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-04-29 10:40:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 01:55:19', '2025-05-17 01:55:19', '2025-05-17 01:55:19');
INSERT INTO `notification` VALUES (22, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：6）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 08:42:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 22:38:21', '2025-05-17 22:38:21', '2025-05-17 22:38:21');
INSERT INTO `notification` VALUES (23, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：5）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-28 15:11:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 22:38:48', '2025-05-17 22:38:48', '2025-05-17 22:38:48');
INSERT INTO `notification` VALUES (24, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：4）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-04-29 08:54:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-17 22:40:52', '2025-05-17 22:40:52', '2025-05-17 22:40:52');
INSERT INTO `notification` VALUES (25, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：3）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-04-29 10:40:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 21:48:19', '2025-05-18 21:48:19', '2025-05-18 21:48:19');
INSERT INTO `notification` VALUES (26, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：7）预约已创建，等待审核。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 16:00:00\n\n您的预约正在审核中，请耐心等待。审核结果将以系统通知的形式告知您。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:37:41', '2025-05-18 22:37:41', '2025-05-18 22:37:41');
INSERT INTO `notification` VALUES (27, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：7）预约审核通过。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 16:00:00\n\n您的预约已通过审核，服务人员将按预约时间上门服务，请保持电话畅通。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:37:52', '2025-05-18 22:37:52', '2025-05-18 22:37:52');
INSERT INTO `notification` VALUES (28, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：7）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 16:00:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:37:55', '2025-05-18 22:37:55', '2025-05-18 22:37:55');
INSERT INTO `notification` VALUES (29, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：7）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 16:00:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:37:57', '2025-05-18 22:37:57', '2025-05-18 22:37:57');
INSERT INTO `notification` VALUES (30, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：8）预约已创建，等待审核。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 10:00:00\n\n您的预约正在审核中，请耐心等待。审核结果将以系统通知的形式告知您。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:39:30', '2025-05-18 22:39:30', '2025-05-18 22:39:30');
INSERT INTO `notification` VALUES (31, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：8）预约审核通过。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 10:00:00\n\n您的预约已通过审核，服务人员将按预约时间上门服务，请保持电话畅通。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:39:34', '2025-05-18 22:39:34', '2025-05-18 22:39:34');
INSERT INTO `notification` VALUES (32, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：8）服务已开始。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 10:00:00\n\n您的服务正在进行中，如有问题请联系客服。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:56:39', '2025-05-18 22:56:39', '2025-05-18 22:56:39');
INSERT INTO `notification` VALUES (33, 1002, '服务预约通知', '尊敬的格拉克结果用户，\n\n您的服务预约（预约号：8）服务已完成。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-21 10:00:00\n\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。\n- 实际服务时长：0分钟\n- 实际服务费用：0.0元\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-18 22:56:52', '2025-05-18 22:56:52', '2025-05-18 22:56:52');
INSERT INTO `notification` VALUES (34, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：9）预约已创建，等待审核。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 09:32:00\n\n您的预约正在审核中，请耐心等待。审核结果将以系统通知的形式告知您。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-19 09:32:12', '2025-05-19 09:32:12', '2025-05-19 09:32:12');
INSERT INTO `notification` VALUES (35, 2, '服务预约通知', '尊敬的刘大毛用户，\n\n您的服务预约（预约号：9）预约审核通过。\n\n预约详情：\n- 服务项目：null\n- 预约时间：2025-05-20 09:32:00\n\n您的预约已通过审核，服务人员将按预约时间上门服务，请保持电话畅通。\n\n如有疑问，请联系客服中心。', 'system', 0, '2025-05-19 09:58:03', '2025-05-19 09:58:03', '2025-05-19 09:58:03');

-- ----------------------------
-- Table structure for physical_exam_report
-- ----------------------------
DROP TABLE IF EXISTS `physical_exam_report`;
CREATE TABLE `physical_exam_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检报告ID',
  `report_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体检报告编号',
  `elder_id` bigint NOT NULL COMMENT '老人ID，关联user表',
  `elder_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '老人姓名',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of physical_exam_report
-- ----------------------------
INSERT INTO `physical_exam_report` VALUES (1, 'PE20240001', 2, '张三', 75, '男', 2, '2024-05-02', '市人民医院', '血脂偏高，建议复查', '/upload/physical_exam/57088d81-7643-4cc0-bc25-6032fa57fcce.pdf', '2025-05-13 17:59:09', '2025-05-15 17:25:21');
INSERT INTO `physical_exam_report` VALUES (2, 'PE20230002', 2, '李四', 77, '女', 2, '2023-11-15', '社区卫生服务中心', '各项指标正常', '/upload/physical_exam/d798d64c-8859-49fb-9477-f483c2163dfd.pdf', '2025-05-13 17:59:09', '2025-05-15 17:30:10');
INSERT INTO `physical_exam_report` VALUES (3, NULL, 1002, '格拉克结果', 57, '男', NULL, '2025-05-05', '长沙市立医院', '肺结核', NULL, '2025-05-19 01:12:09', '2025-05-19 01:12:09');

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
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查项目分类',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `normal_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '正常参考范围',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_report_id`(`report_id` ASC) USING BTREE,
  CONSTRAINT `fk_report_item_report` FOREIGN KEY (`report_id`) REFERENCES `physical_exam_report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检报告指标表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of physical_exam_report_item
-- ----------------------------
INSERT INTO `physical_exam_report_item` VALUES (13, 2, '总胆固醇', '4.8', 'mmol/L', 0, '血脂检查', '注意控制饮食', '3.1-5.7 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (14, 2, '甘油三酯', '1.2', 'mmol/L', 0, '血脂检查', '维持良好生活习惯', '0.56-1.7 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (15, 2, '高密度脂蛋白', '1.3', 'mmol/L', 0, '血脂检查', 'HDL-C俗称好胆固醇', '1.0-1.6 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (16, 2, '低密度脂蛋白', '2.5', 'mmol/L', 0, '血脂检查', 'LDL-C俗称坏胆固醇，需控制在标准范围内', '2.07-3.1 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (17, 1, '总胆固醇', '6.2', 'mmol/L', 1, '血脂检查', '注意控制饮食', '3.1-5.7 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (18, 1, '甘油三酯', '1.8', 'mmol/L', 0, '血脂检查', '维持良好生活习惯', '0.56-1.7 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (19, 1, '高密度脂蛋白', '1.1', 'mmol/L', 0, '血脂检查', 'HDL-C俗称好胆固醇', '1.0-1.6 mmol/L');
INSERT INTO `physical_exam_report_item` VALUES (20, 1, '低密度脂蛋白', '4.0', 'mmol/L', 1, '血脂检查', 'LDL-C俗称坏胆固醇，需控制在标准范围内', '2.07-3.1 mmol/L');

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
INSERT INTO `role` VALUES (1, '老人', 'elder', 1, '2', 1, '0', '0', 'admin', '2025-03-19 15:57:06', 'admin', '2025-05-17 13:30:37', '老人');
INSERT INTO `role` VALUES (2, '家属', 'kin', 2, '2', 1, '0', '0', 'admin', '2025-03-19 15:57:22', 'admin', '2025-05-17 13:30:37', '家属');
INSERT INTO `role` VALUES (3, '社区工作人员', 'staff', 3, '2', 1, '0', '0', 'admin', '2025-03-19 15:57:22', 'admin', '2025-05-17 13:30:37', '社区工作人员');
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
INSERT INTO `role_menu` VALUES (4, 1);
INSERT INTO `role_menu` VALUES (4, 2);
INSERT INTO `role_menu` VALUES (4, 3);
INSERT INTO `role_menu` VALUES (4, 4);
INSERT INTO `role_menu` VALUES (4, 5);
INSERT INTO `role_menu` VALUES (4, 6);
INSERT INTO `role_menu` VALUES (4, 100);
INSERT INTO `role_menu` VALUES (4, 101);
INSERT INTO `role_menu` VALUES (4, 102);
INSERT INTO `role_menu` VALUES (4, 103);
INSERT INTO `role_menu` VALUES (4, 104);
INSERT INTO `role_menu` VALUES (4, 105);
INSERT INTO `role_menu` VALUES (4, 106);
INSERT INTO `role_menu` VALUES (4, 200);
INSERT INTO `role_menu` VALUES (4, 201);
INSERT INTO `role_menu` VALUES (4, 202);
INSERT INTO `role_menu` VALUES (4, 300);
INSERT INTO `role_menu` VALUES (4, 301);
INSERT INTO `role_menu` VALUES (4, 302);
INSERT INTO `role_menu` VALUES (4, 303);
INSERT INTO `role_menu` VALUES (4, 1001);
INSERT INTO `role_menu` VALUES (4, 1002);
INSERT INTO `role_menu` VALUES (4, 1003);
INSERT INTO `role_menu` VALUES (4, 1004);
INSERT INTO `role_menu` VALUES (4, 1005);
INSERT INTO `role_menu` VALUES (4, 1006);
INSERT INTO `role_menu` VALUES (4, 1007);
INSERT INTO `role_menu` VALUES (4, 1008);
INSERT INTO `role_menu` VALUES (4, 1009);
INSERT INTO `role_menu` VALUES (4, 1010);
INSERT INTO `role_menu` VALUES (4, 1011);
INSERT INTO `role_menu` VALUES (4, 1012);
INSERT INTO `role_menu` VALUES (4, 1013);
INSERT INTO `role_menu` VALUES (4, 1014);
INSERT INTO `role_menu` VALUES (4, 1015);
INSERT INTO `role_menu` VALUES (4, 1016);
INSERT INTO `role_menu` VALUES (4, 1017);
INSERT INTO `role_menu` VALUES (4, 1018);
INSERT INTO `role_menu` VALUES (4, 1019);
INSERT INTO `role_menu` VALUES (4, 1020);
INSERT INTO `role_menu` VALUES (4, 1021);
INSERT INTO `role_menu` VALUES (4, 1022);
INSERT INTO `role_menu` VALUES (4, 1023);
INSERT INTO `role_menu` VALUES (4, 1024);
INSERT INTO `role_menu` VALUES (4, 1025);
INSERT INTO `role_menu` VALUES (4, 1026);
INSERT INTO `role_menu` VALUES (4, 1027);
INSERT INTO `role_menu` VALUES (4, 1028);
INSERT INTO `role_menu` VALUES (4, 1029);
INSERT INTO `role_menu` VALUES (4, 1030);
INSERT INTO `role_menu` VALUES (4, 1031);
INSERT INTO `role_menu` VALUES (4, 1032);
INSERT INTO `role_menu` VALUES (4, 1033);
INSERT INTO `role_menu` VALUES (4, 1034);
INSERT INTO `role_menu` VALUES (4, 1035);
INSERT INTO `role_menu` VALUES (4, 1036);
INSERT INTO `role_menu` VALUES (4, 2001);
INSERT INTO `role_menu` VALUES (4, 2002);
INSERT INTO `role_menu` VALUES (4, 2003);
INSERT INTO `role_menu` VALUES (4, 2004);
INSERT INTO `role_menu` VALUES (4, 2005);
INSERT INTO `role_menu` VALUES (4, 2006);
INSERT INTO `role_menu` VALUES (4, 2007);
INSERT INTO `role_menu` VALUES (4, 2008);
INSERT INTO `role_menu` VALUES (4, 2009);
INSERT INTO `role_menu` VALUES (4, 2100);
INSERT INTO `role_menu` VALUES (4, 2101);
INSERT INTO `role_menu` VALUES (4, 2102);
INSERT INTO `role_menu` VALUES (4, 2103);
INSERT INTO `role_menu` VALUES (4, 2104);
INSERT INTO `role_menu` VALUES (4, 2105);
INSERT INTO `role_menu` VALUES (4, 2106);
INSERT INTO `role_menu` VALUES (4, 2107);
INSERT INTO `role_menu` VALUES (4, 2108);
INSERT INTO `role_menu` VALUES (4, 3100);
INSERT INTO `role_menu` VALUES (4, 3101);
INSERT INTO `role_menu` VALUES (4, 3102);
INSERT INTO `role_menu` VALUES (4, 3103);
INSERT INTO `role_menu` VALUES (4, 3104);
INSERT INTO `role_menu` VALUES (4, 3105);
INSERT INTO `role_menu` VALUES (4, 3106);
INSERT INTO `role_menu` VALUES (4, 3107);
INSERT INTO `role_menu` VALUES (4, 3108);
INSERT INTO `role_menu` VALUES (4, 3109);
INSERT INTO `role_menu` VALUES (4, 3110);
INSERT INTO `role_menu` VALUES (4, 3111);
INSERT INTO `role_menu` VALUES (4, 3112);
INSERT INTO `role_menu` VALUES (4, 3113);
INSERT INTO `role_menu` VALUES (4, 3114);
INSERT INTO `role_menu` VALUES (4, 3115);
INSERT INTO `role_menu` VALUES (4, 3116);
INSERT INTO `role_menu` VALUES (4, 3117);
INSERT INTO `role_menu` VALUES (4, 3118);
INSERT INTO `role_menu` VALUES (4, 3119);

-- ----------------------------
-- Table structure for service_item
-- ----------------------------
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item`  (
  `service_id` bigint NOT NULL AUTO_INCREMENT,
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
) ENGINE = InnoDB AUTO_INCREMENT = 4006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_item
-- ----------------------------
INSERT INTO `service_item` VALUES (1002, '药品代购', 'medical', '帮助老人代购处方药品并送药上门', 20.00, 30, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:21', NULL);
INSERT INTO `service_item` VALUES (2001, '全屋清洁', 'cleaning', '包含客厅、卧室、厨房、卫生间全面清洁', 200.00, 180, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:23', NULL);
INSERT INTO `service_item` VALUES (2002, '厨房深度清洁', 'cleaning', '油烟机、灶台等厨房区域专项清洁', 120.00, 90, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:24', NULL);
INSERT INTO `service_item` VALUES (2003, '玻璃清洁', 'cleaning', '室内外窗户玻璃专业清洁', 80.00, 60, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:25', NULL);
INSERT INTO `service_item` VALUES (3001, '水管维修', 'repair', '解决漏水、堵塞等水管问题', 100.00, 60, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:27', NULL);
INSERT INTO `service_item` VALUES (3002, '电路检修', 'repair', '家庭电路安全检查与维修', 150.00, 90, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:27', NULL);
INSERT INTO `service_item` VALUES (3003, '家电维修', 'repair', '冰箱、洗衣机等家电故障维修', 180.00, 120, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:29', NULL);
INSERT INTO `service_item` VALUES (4001, '老人关爱套餐', 'medical', '包含体检+健康咨询+药品代购的套餐服务', 200.00, 180, 1, '2025-03-28 17:00:57', 'admin', 'admin', '2025-03-31 16:40:29', '限时特惠套餐');
INSERT INTO `service_item` VALUES (4003, '萝莉剪发', 'cleaning', '来了来了', 10.00, 30, 1, '2025-05-18 20:18:59', 'admin', 'admin', '2025-05-19 00:28:53', NULL);
INSERT INTO `service_item` VALUES (4004, '全屋清洁', 'cleaning', '哈哈', 20.00, 30, 1, '2025-05-18 20:20:29', 'admin', 'admin', '2025-05-19 00:28:32', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务工单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_order
-- ----------------------------
INSERT INTO `service_order` VALUES (3, 2, 1002, 3, '13433432', '1', '2025-04-29 10:40:00', 0.00, 0, NULL, '2025-04-05 00:39:10', 'admin', '2025-05-18 22:29:49', NULL);
INSERT INTO `service_order` VALUES (4, 2, 3001, 3, '要求认真仔细一点的阿姨', '1', '2025-04-29 08:54:00', 0.00, 0, NULL, '2025-04-05 00:54:48', 'admin', '2025-05-18 22:49:14', NULL);
INSERT INTO `service_order` VALUES (5, 2, 1002, 3, '1111111111111111111', '111', '2025-05-28 15:11:00', 0.00, 0, 'Ldamao', '2025-05-05 17:12:04', 'Ldamao', '2025-05-18 22:29:51', NULL);
INSERT INTO `service_order` VALUES (6, 2, 1002, 3, 'fghfhjgjhhjkghghj', '1111111111111', '2025-05-20 08:42:00', 0.00, 0, 'Ldamao', '2025-05-17 01:42:28', 'Ldamao', '2025-05-18 22:30:01', NULL);
INSERT INTO `service_order` VALUES (7, 1002, 1002, 3, '飞鱼服复古', '已接单', '2025-05-21 16:00:00', 0.00, 0, 'admin', '2025-05-18 22:37:41', 'admin', '2025-05-18 22:37:41', NULL);
INSERT INTO `service_order` VALUES (8, 1002, 1002, 3, '人发范德萨', '已接单', '2025-05-21 10:00:00', 0.00, 0, 'admin', '2025-05-18 22:39:30', 'admin', '2025-05-18 22:39:30', NULL);
INSERT INTO `service_order` VALUES (9, 2, 4004, 1, '2222222222222', '已派单', '2025-05-20 09:32:00', NULL, NULL, 'Ldamao', '2025-05-19 09:32:12', 'Ldamao', '2025-05-19 09:32:12', NULL);

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
INSERT INTO `service_review` VALUES (1, 1002, 3, 2, 0, 0, 5, '服务非常好，医生很专业，态度也很好！强烈推荐！', '2025-05-06 16:30:25', 0, 1, '11111', '2025-05-17 18:50:13', 2, 'Ldamao', 'Ldamao', '首次评价，非常满意', NULL, '2025-05-06 16:30:25', NULL, '2025-05-18 22:30:56', 0);
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
) ENGINE = InnoDB AUTO_INCREMENT = 1005 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'Ldamao', '123456', '刘大毛', '女', '13800000042', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/ldamao.jpg', 1, NULL, NULL, '340881199005204216', '1990-05-20', 35, '张伟', '13812345678', 'serious', 'staff', '2025-03-22 19:46:06', 'admin', '2025-05-15 20:04:22', '');
INSERT INTO `user` VALUES (3, 'Lxiaomao', '123456', '刘小毛', '男', '13800000033', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/lxiaomao.jpg', 1, NULL, NULL, '340881199106155427', '1991-06-15', 34, '刘大毛', '13800000042', '健康状况良好', 'staff', '2025-03-27 15:38:04', 'admin', '2025-05-17 14:25:13', '');
INSERT INTO `user` VALUES (25, 'Lshumei', '123456', '刘淑梅', '女', '15349545835', 'yopsu@mailto.plus', '深圳市福田区福强路100号', '/avatar/lshumei.jpg', 1, NULL, NULL, '12345619900101123X', '1990-01-01', 35, '王小明', '13987654321  ', 'disabled', 'staff', '2025-03-27 15:38:04', 'admin', '2025-05-15 20:04:26', '');
INSERT INTO `user` VALUES (26, 'Wdaniu', '123456', '王大牛', '男', '13458684683', '23468746@qq.com', '深圳市龙岗区龙城大道50号', '/avatar/wdaniu.jpg', 1, NULL, NULL, '440301198508125671', '1985-08-12', 40, '王小花', '13923456789', '健康状况良好', 'admin', '2025-04-06 14:59:16', 'admin', '2025-05-17 16:46:24', '');
INSERT INTO `user` VALUES (999, 'admin', '123456', '超级管理员', '女', '13800000002', 'bob.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/admin.jpg', 1, '系统管理部门', '系统管理员', NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2025-02-26 04:06:56', 'admin', '2025-05-16 17:42:31', '');
INSERT INTO `user` VALUES (1000, 'Zdaye', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOSsz9zY9jUEKQNX.QRJabT7eKDjW', '张大爷', '男', '13800138001', 'zhangdaye@example.com', '北京市海淀区中关村南路1号社区', '/avatar/zhangdaye.jpg', 1, NULL, NULL, '110101195001011234', '1950-01-01', 75, '张小华', '13900139001', 'serious', NULL, '2025-05-15 19:27:07', 'admin', '2025-05-16 17:40:36', '');
INSERT INTO `user` VALUES (1001, 'Zxiaohua', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOSsz9zY9jUEKQNX.QRJabT7eKDjW', '张小华', '女', '13900139002', 'zhangxiaohua@example.com', '北京市朝阳区建国路5号', '/avatar/zhangdaye.jpg', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-05-15 19:27:07', 'admin', '2025-05-17 14:25:21', '');
INSERT INTO `user` VALUES (1002, 'Glakejieguo', '123456', '格拉克结果', '男', '14566776232', 'yopsu@mailto.plus', NULL, NULL, 1, NULL, NULL, '13092419680312201X', '1968-03-12', 57, '格拉木', '13800000043', 'subhealthy', 'admin', '2025-05-15 19:50:27', 'admin', '2025-05-16 17:40:13', '');
INSERT INTO `user` VALUES (1003, 'Glamu', '123456', '格拉木', '男', '13800000043', 'yopsu@mailto.plus', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2025-05-15 20:03:38', 'admin', '2025-05-16 17:42:25', '');

-- ----------------------------
-- Table structure for user_bak
-- ----------------------------
DROP TABLE IF EXISTS `user_bak`;
CREATE TABLE `user_bak`  (
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
-- Records of user_bak
-- ----------------------------
INSERT INTO `user_bak` VALUES (1, 'admin', '123456', '超级管理员', '女', '13800000002', 'bob.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2025-02-26 04:06:56', 'admin', '2025-05-15 17:44:24', '');
INSERT INTO `user_bak` VALUES (2, 'Ldamao', '123456', '刘大毛', '女', '13800000042', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '340881199005204216', '1990-05-20', 35, '张伟', '13812345678', '中等，需定期监测血压、血糖，并注意饮食控制。', 'staff', '2025-03-22 19:46:06', 'admin', '2025-05-15 17:15:49', '');
INSERT INTO `user_bak` VALUES (3, 'Lxiaomao', '123456', '刘小毛', '男', '13800000034', 'mao.builder@hotmail.com', '深圳市南山区科技园中区', '/avatar/zhangdaye.jpg', 1, '', '', '', NULL, NULL, '', '', '', 'staff', '2025-03-27 15:38:04', 'admin', '2025-05-15 17:15:55', '');
INSERT INTO `user_bak` VALUES (25, 'Lshumei', '123456', '刘淑梅', '未知', '15349545835', 'yopsu@mailto.plus', NULL, NULL, 1, '', '', '12345619900101123X', '1990-01-01', 35, '王小明', '13987654321  ', '慢性咳嗽', NULL, NULL, NULL, NULL, '');
INSERT INTO `user_bak` VALUES (26, 'Wdaniu', '123456', '王大牛', '未知', '13458684687', '23468746@qq.com', NULL, NULL, 1, '', '', '', NULL, NULL, '', '', '', 'admin', '2025-04-06 14:59:16', 'admin', '2025-04-06 14:59:16', '');

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
INSERT INTO `user_role` VALUES (2, 1);
INSERT INTO `user_role` VALUES (3, 2);
INSERT INTO `user_role` VALUES (25, 1);
INSERT INTO `user_role` VALUES (26, 2);
INSERT INTO `user_role` VALUES (999, 4);
INSERT INTO `user_role` VALUES (1000, 1);
INSERT INTO `user_role` VALUES (1001, 2);
INSERT INTO `user_role` VALUES (1002, 1);
INSERT INTO `user_role` VALUES (1003, 2);

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
