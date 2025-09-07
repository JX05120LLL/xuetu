/*
 Navicat Premium Dump SQL

 Source Server         : mysql_3306
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : xuetu_db

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 16/08/2025 19:24:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Java编程', 0, 1, '2025-08-13 21:07:18', '2025-08-13 21:33:04');
INSERT INTO `category` VALUES (2, '前端开发', 0, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (3, '数据库', 0, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (4, '移动开发', 0, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (5, '人工智能', 0, 5, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (10, 'Java', 1, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (11, 'Python', 1, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (12, 'JavaScript', 1, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (13, 'C++', 1, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (14, 'Go', 1, 5, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (20, 'Vue.js', 2, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (21, 'React', 2, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (22, 'Angular', 2, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (23, 'HTML/CSS', 2, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (30, 'MySQL', 3, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (31, 'Redis', 3, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (32, 'MongoDB', 3, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (40, 'Android', 4, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (41, 'iOS', 4, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (42, 'Flutter', 4, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (100, 'Java基础', 10, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (101, 'Spring框架', 10, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (102, 'Spring Boot', 10, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `category` VALUES (103, 'MyBatis', 10, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '章节标题',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `chapter_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter
-- ----------------------------
INSERT INTO `chapter` VALUES (1, 1, '第一章：Java基础语法', 1, '2025-08-13 21:07:18', '2025-08-16 16:40:22');
INSERT INTO `chapter` VALUES (2, 1, '第二章：Java基础语法', 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (3, 1, '第三章：面向对象编程', 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (4, 1, '第四章：异常处理与调试', 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (5, 1, '第五章：集合框架详解', 5, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (10, 2, '第一章：Spring Boot快速入门', 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (11, 2, '第二章：自动配置原理', 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (12, 2, '第三章：Web开发实战', 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (13, 2, '第四章：数据访问层开发', 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (14, 2, '第五章：安全与监控', 5, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (20, 6, '第一章：Vue.js 3.0 新特性介绍', 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (21, 6, '第二章：Composition API详解', 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (22, 6, '第三章：组件化开发实战', 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `chapter` VALUES (23, 6, '第四章：路由与状态管理', 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `rating` tinyint NULL DEFAULT NULL COMMENT '评分(1-5)',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `like_count` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  `is_teacher` tinyint NULL DEFAULT 0,
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶(0:否,1:是)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 1, '这门课程很棒，讲解得很详细！', 5, '2025-08-13 22:40:53', '2025-08-13 22:47:59', 1, 1, 0, 0);

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `comment_id` bigint NOT NULL,
  `type` tinyint NOT NULL COMMENT '1:评论点赞,2:回复点赞',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment_like
-- ----------------------------
INSERT INTO `comment_like` VALUES (1, 1, 1, 1, '2025-08-13 22:48:00', '2025-08-13 22:48:00');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '课程价格',
  `original_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '原价',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '讲师ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `level` tinyint NULL DEFAULT 0 COMMENT '难度级别(0:初级,1:中级,2:高级)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:未发布,1:已发布,2:已下架)',
  `total_duration` int NULL DEFAULT 0 COMMENT '总时长(分钟)',
  `student_count` int NULL DEFAULT 0 COMMENT '学习人数',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java零基础入门教程', '从零开始学习Java编程语言，包含语法基础、面向对象编程、异常处理等核心知识点。适合编程初学者，通过大量实战项目快速掌握Java开发技能。', 'https://example.com/covers/java-basic.jpg', 99.00, 199.00, 1, 100, 0, 1, 1200, 1520, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (2, 'Java进阶：Spring Boot实战', '深入学习Spring Boot框架，从入门到精通。包含自动配置、起步依赖、Actuator监控、部署等高级特性。通过实际项目开发掌握企业级开发技能。', 'https://example.com/covers/springboot.jpg', 199.00, 299.00, 1, 102, 1, 1, 1800, 856, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (3, 'Java微服务架构实战', '学习微服务架构设计与实现，包含Spring Cloud、Docker、Kubernetes等技术栈。通过大型项目实战，掌握分布式系统开发与部署。', 'https://example.com/covers/microservice.jpg', 299.00, 399.00, 1, 101, 2, 1, 2400, 423, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (4, 'Python数据分析入门', '学习Python数据分析的核心库：NumPy、Pandas、Matplotlib。从数据清洗到可视化，掌握数据分析的完整流程。', 'https://example.com/covers/python-data.jpg', 149.00, 249.00, 2, 11, 0, 1, 1500, 732, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (5, 'Python机器学习实战', '深入学习机器学习算法与实现，包含监督学习、无监督学习、深度学习等。使用scikit-learn、TensorFlow等主流框架。', 'https://example.com/covers/python-ml.jpg', 399.00, 599.00, 2, 5, 2, 1, 3000, 289, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (6, 'Vue.js 3.0 全家桶开发', '学习Vue.js 3.0的新特性，包含Composition API、Vite构建工具、Vue Router、Vuex状态管理等。通过实战项目掌握现代前端开发。', 'https://example.com/covers/vue3.jpg', 179.00, 279.00, 3, 20, 1, 1, 1600, 945, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (7, 'React企业级开发实战', '深入学习React生态系统，包含Hooks、Redux、React Router、Next.js等。通过大型SPA项目掌握企业级前端开发技能。', 'https://example.com/covers/react.jpg', 229.00, 329.00, 3, 21, 1, 1, 2000, 567, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (8, 'MySQL数据库设计与优化', '系统学习MySQL数据库，从基础语法到高级优化。包含索引设计、查询优化、存储过程、主从复制等企业级技能。', 'https://example.com/covers/mysql.jpg', 129.00, 199.00, 4, 30, 0, 1, 1000, 1234, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (9, 'Redis缓存技术实战', '深入学习Redis缓存技术，包含数据类型、持久化、集群、哨兵等高级特性。掌握高并发系统的缓存设计与优化。', 'https://example.com/covers/redis.jpg', 199.00, 299.00, 4, 31, 1, 1, 1200, 678, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (10, 'Kubernetes容器编排', '学习Kubernetes容器编排技术，包含Pod、Service、Deployment等核心概念。掌握云原生应用的部署与管理。', 'https://example.com/covers/k8s.jpg', 349.00, 449.00, 5, 1, 2, 0, 2200, 0, '2025-08-13 21:07:18', '2025-08-13 21:07:18');

-- ----------------------------
-- Table structure for course_tag
-- ----------------------------
DROP TABLE IF EXISTS `course_tag`;
CREATE TABLE `course_tag`  (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`course_id`, `tag_id`) USING BTREE,
  INDEX `tag_id`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `course_tag_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `course_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_tag
-- ----------------------------
INSERT INTO `course_tag` VALUES (1, 1, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (1, 8, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (1, 11, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (2, 1, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (2, 2, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (2, 10, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (2, 12, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (2, 13, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (3, 1, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (3, 8, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (3, 9, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (4, 4, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (4, 6, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (4, 11, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (5, 4, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (5, 8, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (5, 12, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (6, 3, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (6, 7, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (6, 12, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (6, 14, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (7, 7, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (7, 12, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (7, 13, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (8, 7, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (8, 11, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (9, 9, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (9, 12, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (9, 13, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (10, 8, '2025-08-13 21:12:07');
INSERT INTO `course_tag` VALUES (10, 9, '2025-08-13 21:12:07');

-- ----------------------------
-- Table structure for learning_record
-- ----------------------------
DROP TABLE IF EXISTS `learning_record`;
CREATE TABLE `learning_record`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `lesson_id` bigint NOT NULL COMMENT '课时ID',
  `progress` int NULL DEFAULT 0 COMMENT '学习进度(秒)',
  `is_completed` tinyint NULL DEFAULT 0 COMMENT '是否完成(0:否,1:是)',
  `last_learn_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`, `lesson_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_record
-- ----------------------------

-- 用户1的学习记录（Java零基础入门教程 - 课程ID:1）
INSERT INTO `learning_record` VALUES (1, 1, 1, 900, 1, '2025-01-16 10:35:00', '2025-01-16 10:35:00', '2025-01-16 10:35:00');
INSERT INTO `learning_record` VALUES (1, 1, 2, 1500, 1, '2025-01-16 10:45:00', '2025-01-16 10:35:00', '2025-01-16 10:45:00');
INSERT INTO `learning_record` VALUES (1, 1, 3, 1200, 1, '2025-01-16 11:00:00', '2025-01-16 10:35:00', '2025-01-16 11:00:00');
INSERT INTO `learning_record` VALUES (1, 1, 4, 1200, 0, '2025-01-16 11:15:00', '2025-01-16 10:35:00', '2025-01-16 11:15:00');
INSERT INTO `learning_record` VALUES (1, 1, 10, 800, 0, '2025-01-16 14:20:00', '2025-01-16 10:35:00', '2025-01-16 14:20:00');

-- 用户1的学习记录（Spring Boot实战 - 课程ID:2）
INSERT INTO `learning_record` VALUES (1, 2, 20, 1200, 1, '2025-01-17 09:30:00', '2025-01-17 09:30:00', '2025-01-17 09:30:00');
INSERT INTO `learning_record` VALUES (1, 2, 21, 1800, 0, '2025-01-17 10:00:00', '2025-01-17 09:30:00', '2025-01-17 10:00:00');

-- 用户2的学习记录（Java零基础入门教程 - 课程ID:1）
INSERT INTO `learning_record` VALUES (2, 1, 1, 900, 1, '2025-01-15 09:25:00', '2025-01-15 09:25:00', '2025-01-15 09:25:00');
INSERT INTO `learning_record` VALUES (2, 1, 2, 1500, 1, '2025-01-15 09:40:00', '2025-01-15 09:25:00', '2025-01-15 09:40:00');
INSERT INTO `learning_record` VALUES (2, 1, 3, 1200, 1, '2025-01-15 10:00:00', '2025-01-15 09:25:00', '2025-01-15 10:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 4, 1800, 1, '2025-01-15 10:30:00', '2025-01-15 09:25:00', '2025-01-15 10:30:00');
INSERT INTO `learning_record` VALUES (2, 1, 10, 2100, 1, '2025-01-15 11:00:00', '2025-01-15 09:25:00', '2025-01-15 11:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 11, 1500, 1, '2025-01-15 11:30:00', '2025-01-15 09:25:00', '2025-01-15 11:30:00');
INSERT INTO `learning_record` VALUES (2, 1, 12, 2400, 1, '2025-01-15 12:00:00', '2025-01-15 09:25:00', '2025-01-15 12:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 13, 1800, 0, '2025-01-15 14:30:00', '2025-01-15 09:25:00', '2025-01-15 14:30:00');

-- ----------------------------
-- Table structure for lesson
-- ----------------------------
DROP TABLE IF EXISTS `lesson`;
CREATE TABLE `lesson`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课时ID',
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课时标题',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频URL',
  `duration` int NULL DEFAULT 0 COMMENT '时长(分钟)',
  `is_free` tinyint NULL DEFAULT 0 COMMENT '是否免费(0:否,1:是)',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chapter_id`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `lesson_ibfk_1` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课时表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lesson
-- ----------------------------
INSERT INTO `lesson` VALUES (1, 1, '1.1 课程介绍与学习指南', 'https://example.com/videos/java-1-1.mp4', 15, 1, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (2, 1, '1.2 JDK安装与配置', 'https://example.com/videos/java-1-2.mp4', 25, 1, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (3, 1, '1.3 开发工具IDEA安装', 'https://example.com/videos/java-1-3.mp4', 20, 1, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (4, 1, '1.4 第一个Java程序', 'https://example.com/videos/java-1-4.mp4', 30, 0, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (10, 2, '2.1 Java数据类型详解', 'https://example.com/videos/java-2-1.mp4', 35, 0, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (11, 2, '2.2 变量与常量', 'https://example.com/videos/java-2-2.mp4', 25, 0, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (12, 2, '2.3 运算符与表达式', 'https://example.com/videos/java-2-3.mp4', 40, 0, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (13, 2, '2.4 控制流程语句', 'https://example.com/videos/java-2-4.mp4', 45, 0, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (20, 10, '1.1 Spring Boot简介', 'https://example.com/videos/sb-1-1.mp4', 20, 1, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (21, 10, '1.2 创建第一个Spring Boot项目', 'https://example.com/videos/sb-1-2.mp4', 35, 1, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (22, 10, '1.3 项目结构分析', 'https://example.com/videos/sb-1-3.mp4', 25, 0, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `lesson_id` bigint NOT NULL COMMENT '课时ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记内容',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of note
-- ----------------------------

-- 用户1的笔记
INSERT INTO `note` VALUES (1, 1, 1, 1, 'Java课程学习计划', '今天开始学习Java编程，制定了详细的学习计划：\n1. 每天至少学习2小时\n2. 完成课后练习\n3. 做好学习笔记\n4. 一个月内完成基础部分', '2025-01-16 10:40:00', '2025-01-16 10:40:00');
INSERT INTO `note` VALUES (2, 1, 1, 2, 'JDK安装要点', 'JDK安装过程中需要注意的几个要点：\n1. 选择合适的版本（推荐JDK 17）\n2. 配置环境变量JAVA_HOME\n3. 配置PATH路径\n4. 验证安装：java -version', '2025-01-16 10:50:00', '2025-01-16 10:50:00');
INSERT INTO `note` VALUES (3, 1, 1, 3, 'IDEA使用技巧', 'IntelliJ IDEA常用快捷键：\n- Ctrl+Space：代码补全\n- Ctrl+D：复制当前行\n- Ctrl+Y：删除当前行\n- Ctrl+/：注释/取消注释\n- Shift+F10：运行程序', '2025-01-16 11:05:00', '2025-01-16 11:05:00');
INSERT INTO `note` VALUES (4, 1, 1, 4, '第一个Java程序总结', 'Hello World程序要点：\n1. 类名必须与文件名一致\n2. main方法是程序入口\n3. System.out.println()用于输出\n4. 每个语句以分号结尾\n\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}', '2025-01-16 11:20:00', '2025-01-16 11:20:00');
INSERT INTO `note` VALUES (5, 1, 1, 10, 'Java数据类型笔记', 'Java基本数据类型：\n1. 整数类型：byte, short, int, long\n2. 浮点类型：float, double\n3. 字符类型：char\n4. 布尔类型：boolean\n\n引用数据类型：\n- 类(Class)\n- 接口(Interface)\n- 数组(Array)', '2025-01-16 14:25:00', '2025-01-16 14:25:00');
INSERT INTO `note` VALUES (6, 1, 2, 20, 'Spring Boot简介笔记', 'Spring Boot的优势：\n1. 自动配置：减少样板代码\n2. 起步依赖：简化依赖管理\n3. 内嵌服务器：无需外部容器\n4. 生产就绪：提供监控、健康检查等功能\n\n核心注解：\n@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan', '2025-01-17 09:35:00', '2025-01-17 09:35:00');
INSERT INTO `note` VALUES (7, 1, 2, 21, '创建Spring Boot项目', '创建Spring Boot项目的方式：\n1. 使用Spring Initializr（推荐）\n2. 使用IDE创建\n3. 使用Maven/Gradle手动创建\n\n项目结构：\nsrc/main/java - Java源码\nsrc/main/resources - 配置文件\nsrc/test/java - 测试代码', '2025-01-17 10:05:00', '2025-01-17 10:05:00');

-- 用户2的笔记
INSERT INTO `note` VALUES (8, 2, 1, 1, 'Java学习开始', '开始Java编程之旅！作为一个编程新手，我要认真学习每一个知识点。这门课程看起来很系统，期待能够掌握Java编程技能。', '2025-01-15 09:30:00', '2025-01-15 09:30:00');
INSERT INTO `note` VALUES (9, 2, 1, 2, 'JDK环境配置', '今天学会了JDK的安装和配置，终于可以运行Java程序了！\n重要步骤：\n1. 下载JDK\n2. 安装到指定目录\n3. 设置JAVA_HOME\n4. 添加bin目录到PATH\n测试命令：java -version', '2025-01-15 09:45:00', '2025-01-15 09:45:00');
INSERT INTO `note` VALUES (10, 2, 1, 4, '第一个程序运行成功', '太激动了！第一个Java程序运行成功了！\n虽然只是简单的Hello World，但这是我编程路上的第一步。\n接下来要继续学习更多的Java语法知识。', '2025-01-15 10:35:00', '2025-01-15 10:35:00');
INSERT INTO `note` VALUES (11, 2, 1, 10, 'Java数据类型学习心得', '今天学习了Java的数据类型，感觉内容还是挺多的：\n\n基本类型记忆口诀：\n- 整数：byte short int long（从小到大）\n- 小数：float double（单精度双精度）\n- 字符：char（单个字符）\n- 布尔：boolean（true/false）\n\n需要多练习才能熟练掌握！', '2025-01-15 11:05:00', '2025-01-15 11:05:00');
INSERT INTO `note` VALUES (12, 2, 1, 11, '变量与常量', '变量与常量的区别：\n变量：可以改变值的量\n常量：不能改变值的量（用final修饰）\n\n变量命名规则：\n1. 只能包含字母、数字、下划线、$\n2. 不能以数字开头\n3. 不能使用关键字\n4. 建议使用驼峰命名法', '2025-01-15 11:35:00', '2025-01-15 11:35:00');
INSERT INTO `note` VALUES (13, 2, 1, 12, '运算符总结', 'Java运算符分类：\n1. 算术运算符：+, -, *, /, %\n2. 赋值运算符：=, +=, -=, *=, /=\n3. 比较运算符：==, !=, >, <, >=, <=\n4. 逻辑运算符：&&, ||, !\n5. 位运算符：&, |, ^, ~, <<, >>\n\n注意：++和--的前置与后置区别！', '2025-01-15 12:05:00', '2025-01-15 12:05:00');
INSERT INTO `note` VALUES (14, 2, 1, 13, '控制流程语句重点', '控制流程语句是编程的核心：\n\n1. 条件语句：\n   - if...else\n   - switch...case\n\n2. 循环语句：\n   - for循环\n   - while循环\n   - do...while循环\n\n3. 跳转语句：\n   - break：跳出循环\n   - continue：跳过本次循环\n   - return：返回方法\n\n需要多写代码练习！', '2025-01-15 14:35:00', '2025-01-15 14:35:00');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '订单状态(0:待支付,1:已支付,2:已取消,3:已退款,4:已完成)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `course_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `course_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程封面',
  `price` decimal(10, 2) NOT NULL COMMENT '购买价格',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_type` tinyint NULL DEFAULT 0 COMMENT '支付方式(0:微信,1:支付宝,2:银行卡)',
  `status` tinyint NULL DEFAULT 0 COMMENT '支付状态(0:待支付,1:支付成功,2:支付失败)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment
-- ----------------------------

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付流水号',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_type` tinyint NOT NULL DEFAULT 1 COMMENT '支付方式(0:微信,1:支付宝,2:银行卡)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态(0:待支付,1:支付成功,2:支付失败,3:已退款)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_time`(`payment_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 1, 'PAY202501160001', 199.00, 1, 1, '2025-01-16 10:30:00', '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `payment_record` VALUES (2, 2, 'PAY202501160002', 299.00, 0, 1, '2025-01-16 11:15:00', '2025-08-16 19:14:23', '2025-08-16 19:14:23');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求URL',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission_name`(`permission_name` ASC) USING BTREE,
  UNIQUE INDEX `permission_key`(`permission_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------

-- ----------------------------
-- Table structure for reply
-- ----------------------------
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `user_id` bigint NOT NULL COMMENT '回复用户ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复内容',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `target_user_id` bigint NULL DEFAULT NULL,
  `like_count` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `comment_id`(`comment_id` ASC) USING BTREE,
  CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reply
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
  INDEX `permission_id`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `color` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颜色',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'Java', '#FF5722', '2025-08-13 21:12:07', '2025-08-16 16:28:24');
INSERT INTO `tag` VALUES (2, 'Spring', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (3, 'Vue.js', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (4, 'Python', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (5, '机器学习', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (6, '数据分析', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (7, '前端', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (8, '后端', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (9, '数据库', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (10, '微服务', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (11, '初学者友好', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (12, '项目实战', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (13, '企业级', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (14, '热门', NULL, '2025-08-13 21:12:07', '2025-08-13 21:12:07');
INSERT INTO `tag` VALUES (15, 'MySql', '#FF5722', '2025-08-16 16:29:12', '2025-08-16 16:29:12');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密存储)',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'testuser', '$2a$10$/yNXOBuh7vIp.N0XUw/7CedVDc8zWqwHbcys5ZJqt7UyHufOEYkGi', 'test@example.com', '13888888888', '测试用户', NULL, 1, '2025-08-13 19:51:53', '2025-08-13 19:51:53');

-- ----------------------------
-- Table structure for user_course
-- ----------------------------
DROP TABLE IF EXISTS `user_course`;
CREATE TABLE `user_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户课程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `purchase_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '购买价格',
  `purchase_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `progress` int NOT NULL DEFAULT 0 COMMENT '学习进度(0-100)',
  `last_learn_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:未开通,1:正常,2:过期,3:禁用)',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_purchase_time`(`purchase_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户课程关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_course
-- ----------------------------
INSERT INTO `user_course` VALUES (1, 1, 1, 1, 199.00, '2025-01-16 10:31:00', 25, NULL, 1, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `user_course` VALUES (2, 1, 2, 2, 299.00, '2025-01-16 11:16:00', 0, NULL, 1, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `user_course` VALUES (3, 2, 1, NULL, 199.00, '2025-01-15 09:20:00', 80, NULL, 1, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
