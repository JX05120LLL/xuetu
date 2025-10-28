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

 Date: 26/10/2025 18:48:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_analysis_report
-- ----------------------------
DROP TABLE IF EXISTS `ai_analysis_report`;
CREATE TABLE `ai_analysis_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` tinyint NOT NULL COMMENT '报告类型: 1-学习分析,2-课程推荐,3-学习计划',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告内容(JSON格式)',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报告摘要',
  `learning_time` int NULL DEFAULT 0 COMMENT '统计的学习时长(分钟)',
  `completed_courses` int NULL DEFAULT 0 COMMENT '完成的课程数',
  `continuous_days` int NULL DEFAULT 0 COMMENT '连续学习天数',
  `advice_count` int NULL DEFAULT 0 COMMENT '建议条数',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_report_type`(`report_type` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI分析报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_analysis_report
-- ----------------------------

-- ----------------------------
-- Table structure for ai_faq
-- ----------------------------
DROP TABLE IF EXISTS `ai_faq`;
CREATE TABLE `ai_faq`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类: 学习方法,编程问题,课程相关',
  `question` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'AI生成的答案',
  `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用,1-启用',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_use_count`(`use_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI常见问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_faq
-- ----------------------------
INSERT INTO `ai_faq` VALUES (1, '学习方法', '如何高效学习编程？', 'AI建议：1. 每天坚持编码实践 2. 做项目巩固知识 3. 多看优秀代码 4. 保持学习热情', 0, 1, '2025-10-02 16:20:29', '2025-10-02 16:20:29');
INSERT INTO `ai_faq` VALUES (2, '学习方法', '零基础应该从哪里开始学Java？', 'AI建议：建议按照以下路径学习：1. Java基础语法 2. 面向对象编程 3. 集合框架 4. IO流和多线程 5. 数据库操作 6. Web开发框架', 0, 1, '2025-10-02 16:20:29', '2025-10-02 16:20:29');
INSERT INTO `ai_faq` VALUES (3, '编程问题', '什么是Spring Boot？', 'Spring Boot是Spring框架的扩展，它简化了Spring应用的初始搭建以及开发过程。主要特点：1. 自动配置 2. 独立运行 3. 无需XML配置 4. 内嵌服务器', 0, 1, '2025-10-02 16:20:29', '2025-10-02 16:20:29');
INSERT INTO `ai_faq` VALUES (4, '编程问题', 'NullPointerException怎么解决？', 'AI建议：1. 使用对象前先判空 2. 使用Optional避免null 3. 使用@NonNull注解 4. 检查返回值 5. 使用断言', 0, 1, '2025-10-02 16:20:29', '2025-10-02 16:20:29');
INSERT INTO `ai_faq` VALUES (5, '课程相关', '学完Java基础应该学什么？', 'AI建议：建议学习路径：Java基础 → 数据库MySQL → Web开发(SpringBoot) → 前端基础 → 项目实战', 0, 1, '2025-10-02 16:20:29', '2025-10-02 16:20:29');

-- ----------------------------
-- Table structure for ai_recommendation
-- ----------------------------
DROP TABLE IF EXISTS `ai_recommendation`;
CREATE TABLE `ai_recommendation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '推荐的课程ID',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI推荐理由',
  `score` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '推荐分数(0-1)',
  `is_clicked` tinyint NULL DEFAULT 0 COMMENT '是否点击: 0-未点击,1-已点击',
  `is_purchased` tinyint NULL DEFAULT 0 COMMENT '是否购买: 0-未购买,1-已购买',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `clicked_time` datetime NULL DEFAULT NULL COMMENT '点击时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI智能推荐记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_recommendation
-- ----------------------------

-- ----------------------------
-- Table structure for ai_usage_stats
-- ----------------------------
DROP TABLE IF EXISTS `ai_usage_stats`;
CREATE TABLE `ai_usage_stats`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `date` date NOT NULL COMMENT '日期',
  `chat_count` int NULL DEFAULT 0 COMMENT '对话次数',
  `total_tokens` int NULL DEFAULT 0 COMMENT '总消耗tokens',
  `analysis_count` int NULL DEFAULT 0 COMMENT '分析报告次数',
  `recommendation_count` int NULL DEFAULT 0 COMMENT '推荐课程次数',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `date` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI使用统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_usage_stats
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Java编程', 0, 1, '2025-08-13 21:07:18', '2025-10-05 15:41:28');
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
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '章节表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `chapter` VALUES (30, 3, '第一章：微服务架构基础', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (31, 3, '第二章：Spring Cloud核心组件', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (32, 3, '第三章：服务注册与发现', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (33, 3, '第四章：Docker容器化部署', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (34, 3, '第五章：Kubernetes实战', 5, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (40, 4, '第一章：Python数据分析基础', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (41, 4, '第二章：NumPy数组计算', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (42, 4, '第三章：Pandas数据处理', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (43, 4, '第四章：数据可视化Matplotlib', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (50, 5, '第一章：机器学习基础', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (51, 5, '第二章：监督学习算法', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (52, 5, '第三章：无监督学习', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (53, 5, '第四章：深度学习入门', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (54, 5, '第五章：项目实战', 5, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (70, 7, '第一章：React基础回顾', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (71, 7, '第二章：React Hooks深入', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (72, 7, '第三章：Redux状态管理', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (73, 7, '第四章：React Router路由', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (74, 7, '第五章：企业级项目实战', 5, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (80, 8, '第一章：MySQL基础', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (81, 8, '第二章：数据库设计', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (82, 8, '第三章：索引与优化', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (83, 8, '第四章：高级特性', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (90, 9, '第一章：Redis入门', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (91, 9, '第二章：Redis数据类型', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (92, 9, '第三章：Redis高级特性', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (93, 9, '第四章：缓存设计实战', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (100, 10, '第一章：K8s基础概念', 1, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (101, 10, '第二章：核心组件详解', 2, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (102, 10, '第三章：应用部署实战', 3, '2025-10-18 17:40:41', '2025-10-18 17:40:41');
INSERT INTO `chapter` VALUES (103, 10, '第四章：高级特性', 4, '2025-10-18 17:40:41', '2025-10-18 17:40:41');

-- ----------------------------
-- Table structure for chat_history
-- ----------------------------
DROP TABLE IF EXISTS `chat_history`;
CREATE TABLE `chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '对话历史ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `conversation_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色: user(用户)/assistant(AI)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID(可选)',
  `lesson_id` bigint NULL DEFAULT NULL COMMENT '关联课时ID(可选)',
  `tokens` int NULL DEFAULT 0 COMMENT '消耗的tokens数',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'deepseek-chat' COMMENT '使用的AI模型',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_history
-- ----------------------------
INSERT INTO `chat_history` VALUES (1, 1, 'conv-123456', 'user', '什么是Spring Boot?', 1, 1, 0, 'qwen-turbo', '2025-10-02 16:26:54');
INSERT INTO `chat_history` VALUES (2, 1, 'conv-123456', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。很高兴为你解答问题！\n\n**Spring Boot** 是一个基于 **Spring Framework** 的开源 Java 框架，它的主要目的是简化 Spring 应用程序的开发和部署。简单来说，它让开发者可以更快速、更方便地创建独立的、生产级的 Spring 应用。\n\n### Spring Boot 的特点：\n\n1. **开箱即用**  \n   Spring Boot 提供了默认配置，不需要你手动写很多繁琐的 XML 或注解配置，大大减少了开发时间。\n\n2. **内嵌服务器**  \n   它支持内嵌的 Tomcat、Jetty 或 Undertow 服务器，你可以直接运行一个 `.jar` 文件启动应用，而不需要额外安装服务器。\n\n3. **自动配置**  \n   Spring Boot 根据你添加的依赖自动配置你的应用，比如如果你引入了数据库依赖，它会自动配置数据源。\n\n4. **简化依赖管理**  \n   使用 Spring Boot 的 `starter` 依赖（如 `spring-boot-starter-web`、`spring-boot-starter-data-jpa` 等），可以快速引入常用功能模块。\n\n5. **健康检查和监控**  \n   Spring Boot 提供了 `/actuator` 接口，可以方便地进行应用监控和健康检查。\n\n---\n\n### 举个例子：\n如果你想写一个简单的 Web 应用，使用 Spring Boot 只需要几行代码就可以实现：\n\n```java\n@SpringBootApplication\npublic class MyApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(MyApplication.class, args);\n    }\n}\n\n@RestController\npublic class HelloController {\n    @GetMapping(\"/hello\")\n    public String hello() {\n        return \"Hello, Spring Boot!\";\n    }\n}\n```\n\n运行这个程序后，访问 `http://localhost:8080/hello` 就能看到输出。\n\n---\n\n### 适合学习 Spring Boot 的人：\n- 学习 Java 后想进一步做 Web 开发的同学。\n- 想快速上手企业级项目开发的人。\n- 对 Java 后端开发感兴趣的学习者。\n\n---\n\n如果你对 Spring Boot 感兴趣，我还可以推荐一些适合初学者的学习课程哦！你想了解哪些内容呢？比如：如何搭建环境、如何开发 RESTful API、如何连接数据库等？', 1, 1, 0, 'qwen-turbo', '2025-10-02 16:26:55');
INSERT INTO `chat_history` VALUES (3, 1, 'conv-123456', 'user', '什么是Java?', 1, 1, 0, 'qwen-turbo', '2025-10-02 16:30:27');
INSERT INTO `chat_history` VALUES (4, 1, 'conv-123456', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。很高兴为你解答问题！\n\n**Java** 是一种广泛使用的高级编程语言，由 Sun Microsystems（现在属于 Oracle 公司）于 1995 年推出。Java 的设计目标是让开发者能够“一次编写，到处运行”（Write Once, Run Anywhere），这得益于它的 **跨平台能力**。\n\n---\n\n### 🔹 Java 的特点：\n\n1. **跨平台性**  \n   Java 程序不是直接编译成机器码，而是先编译成一种中间代码（字节码），然后在 **Java 虚拟机（JVM）** 上运行。只要系统上有 JVM，就可以运行 Java 程序，所以它可以在 Windows、Linux、Mac 等各种操作系统上运行。\n\n2. **面向对象**  \n   Java 是一种 **面向对象的编程语言（OOP）**，支持类、对象、继承、封装、多态等特性，使代码更清晰、易维护。\n\n3. **简单易学**  \n   Java 的语法相对简洁，不像 C++ 那样复杂，适合初学者入门。\n\n4. **安全性高**  \n   Java 提供了多种安全机制，比如内存管理（自动垃圾回收）、访问控制等，减少了程序出错的可能性。\n\n5. **强大的生态系统**  \n   Java 拥有庞大的开发工具、框架和库，比如 Spring、Hibernate、MyBatis 等，适用于各种类型的应用开发。\n\n---\n\n### 🔹 Java 的应用领域：\n\n- **Web 开发**：使用 Java 开发网站后端（如 Spring Boot、Servlet、JSP 等）。\n- **移动应用**：Android 应用开发主要使用 Java 或 Kotlin。\n- **企业级应用**：很多大型企业的后端系统都是用 Java 构建的。\n- **大数据技术**：Hadoop、Spark 等大数据框架也基于 Java。\n- **金融、电信、医疗等行业**：Java 在这些行业中有广泛应用。\n\n---\n\n### 🔹 Java 的基本结构示例：\n\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n```\n\n这段代码会在控制台输出 `Hello, World!`，这就是一个最简单的 Java 程序。\n\n---\n\n### 🔹 学习 Java 的建议：\n\n- 如果你是编程小白，可以从基础语法开始学起，比如变量、循环、条件语句、类和对象等。\n- 推荐学习一些主流的 Java 开发框架，比如：\n  - **Spring**：用于企业级应用开发。\n  - **Spring Boot**：简化 Spring 应用的开发。\n  - **Hibernate**：用于数据库操作。\n- 可以结合项目实战来巩固知识，比如做一个图书管理系统、博客系统等。\n\n---\n\n如果你对 Java 感兴趣，我还可以推荐一些适合初学者的学习课程哦！你想从哪里开始学？比如：Java 基础语法、面向对象编程、Web 开发等？', 1, 1, 0, 'qwen-turbo', '2025-10-02 16:30:27');
INSERT INTO `chat_history` VALUES (5, 104, 'ae3bbfa5-6741-4d50-91a1-1e9f59d3dd16', 'user', '推荐适合我的课程', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:11:35');
INSERT INTO `chat_history` VALUES (6, 104, 'ae3bbfa5-6741-4d50-91a1-1e9f59d3dd16', 'assistant', '你好呀！我是小途，学途教育平台的AI学习助手。为了给你推荐最适合的课程，我需要先了解一些你的具体情况哦！\n\n你可以告诉我：\n1. **你目前的年级或学习阶段**（比如小学、初中、高中、大学，或者正在备考某个考试）？\n2. **你感兴趣的学习领域**（比如数学、英语、编程、艺术、科学等）？\n3. **你希望提升哪些方面**（比如成绩提高、兴趣培养、考前冲刺、技能学习等）？\n4. **有没有特别想学的内容或目标**（比如准备高考、雅思、编程入门、画画、音乐等）？\n\n这样我就能为你量身定制一套适合的学习方案啦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:11:35');
INSERT INTO `chat_history` VALUES (7, 104, '48d50c7b-7954-44ad-8ab1-5422535acaf7', 'user', '分析我的学习情况', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:11:52');
INSERT INTO `chat_history` VALUES (8, 104, '48d50c7b-7954-44ad-8ab1-5422535acaf7', 'assistant', '你好呀！我是小途，你的学习助手。为了更好地分析你的学习情况，我需要先了解一些基本信息：\n\n1. 你目前的年级和学科（比如：初中数学、高中物理等）？\n2. 你最近在学习哪些内容？有没有特别感兴趣的科目？\n3. 你觉得自己在学习中遇到的主要困难是什么？（比如：理解不了知识点、做题慢、记不住公式等）\n4. 你平时的学习方式是怎样的？（比如：自学、上课听讲、刷题、看网课等）\n\n如果你愿意分享这些信息，我可以根据你的具体情况给出更精准的学习建议和课程推荐哦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:11:52');
INSERT INTO `chat_history` VALUES (9, 104, '2de4faf4-c21b-4f12-8d80-045ddbd7e507', 'user', '如何高效学习编程', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:12:09');
INSERT INTO `chat_history` VALUES (10, 104, '2de4faf4-c21b-4f12-8d80-045ddbd7e507', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。很高兴你对编程感兴趣！学习编程其实就像学一门新语言一样，只要方法得当，就能高效掌握。下面是一些高效学习编程的建议，希望对你有帮助：\n\n---\n\n### 一、明确目标，选择适合的语言\n编程有很多语言，比如：\n- **Python**：适合初学者，语法简单，应用广泛（数据分析、人工智能等）\n- **Java**：适合学习面向对象编程，常用于企业开发\n- **JavaScript**：适合前端开发，也可以做后端（Node.js）\n- **C/C++**：适合理解底层逻辑，常用于游戏开发或系统编程\n\n👉 **建议**：根据你的兴趣和目标选择一门语言开始学习，不要一开始就“贪多”。\n\n---\n\n### 二、打好基础，循序渐进\n1. **理解基本语法**：如变量、循环、条件语句、函数等。\n2. **掌握数据结构与算法**：这是编程的核心，能提升解决问题的能力。\n3. **了解编程思想**：比如面向对象、函数式编程、模块化设计等。\n\n📌 小贴士：可以使用一些在线平台（如LeetCode、Codecademy、慕课网）来练习。\n\n---\n\n### 三、多写代码，动手实践\n编程不是看出来的，是“写”出来的！\n\n- 每天坚持写一点代码，哪怕只是一个小程序。\n- 做项目比刷题更有成就感，比如：\n  - 写一个计算器\n  - 开发一个简单的待办事项列表\n  - 制作一个个人博客网站\n\n💡 **记住**：遇到问题时，不要怕查资料，多问、多试、多改。\n\n---\n\n### 四、善用资源和工具\n- **学习平台**：如B站、慕课网、Coursera、Udemy等\n- **代码社区**：GitHub、Stack Overflow、知乎、掘金等\n- **IDE工具**：如VS Code、PyCharm、Sublime Text等\n\n---\n\n### 五、养成良好的学习习惯\n- **定期复习**：巩固所学内容\n- **做笔记**：记录关键知识点和易错点\n- **参与交流**：加入编程学习群组，和他人一起进步\n\n---\n\n### 六、推荐课程（如果你需要）\n如果你正在找系统的学习路径，我也可以帮你推荐适合的课程哦！比如：\n- Python入门课程\n- Web开发全栈课程\n- 数据分析/人工智能方向课程\n\n你想从哪个方向开始呢？我可以根据你的需求给你更具体的建议！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-14 18:12:09');
INSERT INTO `chat_history` VALUES (11, 103, '3c0fc45a-0669-4294-a913-58a094325117', 'user', '你好，我叫周文，我怎么成为架构师', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:55:18');
INSERT INTO `chat_history` VALUES (12, 103, '3c0fc45a-0669-4294-a913-58a094325117', 'assistant', '你好，周文！很高兴认识你，我是小途，学途教育平台的AI学习助手。你想成为架构师，这是一个非常有挑战性也很有成就感的职业方向。下面我来给你详细说说，如何一步步成长为一名优秀的架构师。\n\n---\n\n### 一、什么是架构师？\n\n架构师是负责设计系统整体结构的人，他们需要考虑系统的性能、可扩展性、安全性、可维护性等多方面因素。架构师通常不直接写代码，但需要对技术有深入的理解，并能指导开发团队进行开发。\n\n---\n\n### 二、成为架构师的基本路径\n\n#### 1. **打好编程基础**\n- 学好一门或几门编程语言（如 Java、Python、C++、JavaScript 等）。\n- 掌握数据结构与算法，这是解决复杂问题的基础。\n- 熟悉常用开发工具和版本控制（如 Git）。\n\n👉 **推荐课程：**  \n- 编程入门（如《Java编程基础》）\n- 数据结构与算法（如《算法导论》）\n\n#### 2. **掌握软件工程知识**\n- 学习软件开发流程（如敏捷开发、DevOps）\n- 了解系统设计原则（如 SOLID 原则）\n- 熟悉常见的设计模式（如工厂模式、单例模式、观察者模式等）\n\n👉 **推荐课程：**  \n- 《软件工程导论》\n- 《设计模式精讲》\n\n#### 3. **深入了解系统架构**\n- 学习微服务、分布式系统、云原生等现代架构设计理念。\n- 熟悉主流架构框架（如 Spring Cloud、Kubernetes、Docker 等）。\n- 了解数据库设计、缓存策略、负载均衡等技术。\n\n👉 **推荐课程：**  \n- 《微服务架构实战》\n- 《云原生架构设计》\n\n#### 4. **积累项目经验**\n- 多参与实际项目，尤其是中大型项目的开发。\n- 尝试从设计阶段开始参与，理解整个系统是如何构建的。\n- 通过实践不断优化自己的设计能力。\n\n👉 **推荐课程：**  \n- 《企业级项目实战》\n- 《全栈开发项目实践》\n\n#### 5. **持续学习与提升**\n- 架构师需要紧跟技术趋势，比如 AI、大数据、区块链等新技术。\n- 阅读经典书籍（如《设计数据密集型应用》《架构师修炼之道》等）。\n- 参加技术社区、技术大会，与同行交流。\n\n---\n\n### 三、推荐的学习路线图（以 Java 为例）\n\n| 阶段 | 内容 | 目标 |\n|------|------|------|\n| 初级 | 编程基础、数据结构与算法 | 能够独立完成简单项目 |\n| 中级 | 软件工程、设计模式、数据库设计 | 能够参与中型项目开发 |\n| 高级 | 分布式系统、微服务、云原生 | 能够设计并实现复杂系统 |\n| 架构师 | 系统架构设计、性能调优、技术选型 | 能够主导大型系统设计 |\n\n---\n\n### 四、有哪些适合你的课程推荐？\n\n如果你正在准备成为架构师，我建议你先从以下几门课程入手：\n\n1. **《Java架构师成长计划》**  \n   - 内容：涵盖 Java 基础、Spring 框架、微服务、分布式系统等\n   - 适合对象：有一定 Java 开发基础，想向架构师方向发展的同学\n\n2. **《系统架构设计与实践》**  \n   - 内容：系统设计原理、架构模式、高并发场景设计等\n   - 适合对象：希望提升系统设计能力的同学\n\n3. **《云原生架构与 DevOps 实践》**  \n   - 内容：容器化、Kubernetes、CI/CD 等\n   - 适合对象：想进入互联网大厂或云计算领域的同学\n\n---\n\n### 五、你目前的水平如何？是否已经有编程基础？\n\n如果你愿意告诉我你现在的能力水平（比如是否会写代码、有没有做过项目、学过哪些技术），我可以为你定制一个更具体的提升计划哦！\n\n---\n\n如果你有任何疑问，或者需要我帮你规划学习路径，随时来找我！祝你早日成为优秀的架构师！🚀', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:55:19');
INSERT INTO `chat_history` VALUES (13, 103, '3c0fc45a-0669-4294-a913-58a094325117', 'user', '你好，我叫周文，我该如何学习编程，\n我怎么成为架构师', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:12');
INSERT INTO `chat_history` VALUES (14, 103, '3c0fc45a-0669-4294-a913-58a094325117', 'assistant', '你好，周文！很高兴再次见到你。你是想**学习编程**，并最终成为**架构师**，这是一个非常有前途的方向！我来为你详细规划一下，从零基础开始，如何一步步走向架构师的路径。\n\n---\n\n## 🧭 一、学习编程的基本步骤\n\n### 1. **明确目标：你想用编程做什么？**\n- 比如：做网页开发、App 开发、数据分析、人工智能、后端系统开发等。\n- 不同方向需要掌握不同的语言和工具。\n\n👉 **建议**：如果你刚开始学习，可以先选择一门**广泛使用的语言**作为入门，比如：\n- **Python**（适合初学者，应用广泛）\n- **Java**（企业级应用、安卓开发、架构方向常用）\n- **JavaScript**（前端开发、全栈开发）\n\n### 2. **学习基础知识**\n无论选择哪种语言，都需要掌握以下基础内容：\n\n| 内容 | 说明 |\n|------|------|\n| 编程语法 | 变量、循环、条件语句、函数等 |\n| 数据结构 | 数组、链表、栈、队列、字典等 |\n| 算法基础 | 排序、查找、递归、动态规划等 |\n| 面向对象编程（OOP） | 类、对象、继承、多态等 |\n\n👉 **推荐学习资源：**\n- 免费平台：[Codecademy](https://www.codecademy.com/)、[freeCodeCamp](https://www.freecodecamp.org/)\n- 视频课程：B站、YouTube 上的编程入门教程\n- 书籍：《Python编程：从入门到实践》《算法导论》\n\n### 3. **动手写代码**\n- 学完一个知识点，就尝试自己写一个小程序。\n- 做一些小项目：比如计算器、猜数字游戏、待办事项管理器等。\n\n---\n\n## 🌐 二、进阶学习：从程序员到架构师\n\n### 1. **掌握软件工程与系统设计**\n- 学习软件开发流程（如敏捷开发、Scrum）\n- 学习系统设计原则（SOLID、DRY、KISS 等）\n- 学习常见设计模式（工厂模式、策略模式、观察者模式等）\n\n👉 **推荐课程：**\n- 《软件工程导论》\n- 《设计模式精讲》\n\n### 2. **深入了解系统架构**\n- 学习分布式系统、微服务、云原生等现代架构理念\n- 熟悉主流技术栈（如 Spring Cloud、Docker、Kubernetes、Redis、MQ 等）\n\n👉 **推荐课程：**\n- 《微服务架构实战》\n- 《云原生架构设计》\n\n### 3. **参与项目实践**\n- 多参与实际项目，尤其是中大型项目。\n- 尝试从设计阶段参与，理解整个系统的构建过程。\n- 通过实践不断优化自己的设计能力。\n\n👉 **推荐课程：**\n- 《企业级项目实战》\n- 《全栈开发项目实践》\n\n### 4. **持续学习与提升**\n- 架构师需要紧跟技术趋势，比如 AI、大数据、区块链等新技术。\n- 阅读经典书籍（如《设计数据密集型应用》《架构师修炼之道》等）。\n- 参加技术社区、技术大会，与同行交流。\n\n---\n\n## 🎯 三、推荐的学习路线图（以 Java 为例）\n\n| 阶段 | 内容 | 目标 |\n|------|------|------|\n| 初级 | 编程基础、数据结构与算法 | 能够独立完成简单项目 |\n| 中级 | 软件工程、设计模式、数据库设计 | 能够参与中型项目开发 |\n| 高级 | 分布式系统、微服务、云原生 | 能够设计并实现复杂系统 |\n| 架构师 | 系统架构设计、性能调优、技术选型 | 能够主导大型系统设计 |\n\n---\n\n## 📚 四、适合你的课程推荐（根据你目前情况）\n\n如果你是**零基础**，我们可以这样安排：\n\n### ✅ 第一阶段：编程入门\n- 课程：《Python编程入门》或《Java编程基础》\n- 时间：2~3个月\n- 目标：掌握编程基础，能写出简单程序\n\n### ✅ 第二阶段：进阶开发\n- 课程：《Java高级编程》《Spring Boot 入门》\n- 时间：3~6个月\n- 目标：能够开发小型 Web 应用，理解 MVC 架构\n\n### ✅ 第三阶段：系统设计与架构\n- 课程：《系统架构设计与实践》《微服务架构实战》\n- 时间：6~12个月\n- 目标：具备系统设计能力，能够参与架构设计\n\n---\n\n## 💡 五、你需要哪些帮助？\n\n你可以告诉我：\n- 你是否有编程经验？\n- 你更感兴趣的是哪个方向（比如前端、后端、全栈、AI）？\n- 你希望在什么时间范围内达到什么样的目标？\n\n我会根据你的具体情况，为你定制一份**专属的学习计划**！\n\n---\n\n如果你想现在就开始学习，我可以推荐一些**免费资源**或者**学途教育的入门课程**给你。随时告诉我你的想法哦！😊\n\n祝你学习顺利，早日成为一名优秀的架构师！🚀', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:12');
INSERT INTO `chat_history` VALUES (15, 103, '2495bc46-ad81-48a8-b33a-de4129a6b649', 'user', '推荐适合我的课程', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:19');
INSERT INTO `chat_history` VALUES (16, 103, '2495bc46-ad81-48a8-b33a-de4129a6b649', 'assistant', '你好呀！我是小途，很高兴为你服务。为了给你推荐最适合的课程，我需要先了解一些关于你的信息哦。\n\n你可以告诉我：\n1. 你目前的年级或学习阶段（比如小学、初中、高中、大学等）？\n2. 你对哪些科目比较感兴趣或者需要帮助？（比如数学、语文、英语、物理、化学等）\n3. 有没有特别想提高的方面？比如考试成绩、写作能力、口语表达、逻辑思维等？\n4. 你希望课程是线上还是线下？或者更倾向于哪种学习方式？（比如直播课、录播课、一对一辅导等）\n\n这样我就能为你量身定制最适合的学习计划啦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:19');
INSERT INTO `chat_history` VALUES (17, 103, '2495bc46-ad81-48a8-b33a-de4129a6b649', 'user', '我想成为Java架构师', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:43');
INSERT INTO `chat_history` VALUES (18, 103, '2495bc46-ad81-48a8-b33a-de4129a6b649', 'assistant', '太棒了！想成为Java架构师是一个非常有前景的目标！作为架构师，你需要掌握扎实的编程基础、系统设计能力以及对大型应用的架构理解。接下来我为你推荐一些适合你的课程和学习路径，帮助你一步步走向Java架构师的道路。\n\n### 一、学习路径建议\n\n#### 1. **基础阶段（掌握Java核心技能）**\n- **目标**：熟练掌握Java语言本身，包括语法、集合、多线程、IO、JVM等。\n- **推荐课程**：\n  - 《Java编程思想》（书籍）\n  - 《Java核心技术卷Ⅰ》（书籍）\n  - 学途教育的《Java开发入门与进阶》课程\n  - 《Java高级编程》课程（涉及面向对象、泛型、异常处理等）\n\n#### 2. **进阶阶段（深入Java技术栈）**\n- **目标**：熟悉Java在企业级开发中的应用，如Spring框架、数据库操作、Web开发等。\n- **推荐课程**：\n  - 《Spring框架实战》课程\n  - 《MyBatis与MySQL数据库开发》课程\n  - 《Java Web开发实战》课程\n  - 《Java高并发与多线程编程》课程\n\n#### 3. **架构阶段（掌握系统设计与架构思维）**\n- **目标**：学习如何设计可扩展、高性能、稳定的系统架构。\n- **推荐课程**：\n  - 《Java分布式系统设计》课程\n  - 《微服务架构（Spring Cloud）》课程\n  - 《高并发系统设计与优化》课程\n  - 《架构师必修课：系统设计与性能调优》课程\n\n#### 4. **拓展阶段（提升综合能力）**\n- **目标**：了解DevOps、云原生、容器化（如Docker、Kubernetes）、大数据等相关技术。\n- **推荐课程**：\n  - 《Docker与Kubernetes实战》课程\n  - 《云原生与Spring Cloud Alibaba》课程\n  - 《大数据与Hadoop生态》课程\n  - 《AI与机器学习基础》课程（选修，拓宽视野）\n\n---\n\n### 二、学习建议\n\n1. **打好基础**：不要急于追求“架构师”，先练好Java基本功。\n2. **动手实践**：多做项目，尝试自己搭建小型系统或参与开源项目。\n3. **阅读经典书籍**：如《设计模式：可复用面向对象软件的基础》、《架构整洁之道》等。\n4. **关注行业动态**：关注Java社区、技术博客（如掘金、CSDN、知乎），了解最新的技术趋势。\n5. **考取认证**：如Oracle Java认证（OCP）、阿里云ACP、AWS相关认证等，增加竞争力。\n\n---\n\n### 三、推荐课程（学途教育）\n\n| 课程名称 | 适合人群 | 内容概要 |\n|----------|----------|----------|\n| Java开发入门与进阶 | 初学者 | Java基础语法、面向对象、集合、多线程等 |\n| Spring框架实战 | 中级开发者 | Spring、Spring Boot、Spring MVC等 |\n| Java分布式系统设计 | 高级开发者 | 微服务、分布式事务、负载均衡等 |\n| 架构师必修课：系统设计与性能调优 | 有经验开发者 | 系统设计方法、性能优化、高可用性设计 |\n\n---\n\n如果你愿意，我可以根据你的当前水平，帮你制定一个更详细的学习计划。你目前是刚入门还是有一定Java基础呢？😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-15 19:56:43');
INSERT INTO `chat_history` VALUES (19, 103, '1d58aa33-cd24-4693-af0b-332f0499ef98', 'user', '什么是微服务，什么是分布式', NULL, NULL, 0, 'qwen-turbo', '2025-10-18 18:26:21');
INSERT INTO `chat_history` VALUES (20, 103, '1d58aa33-cd24-4693-af0b-332f0499ef98', 'assistant', '你好呀，我是小途，很高兴为你解答学习中的问题！\n\n我们来一起了解一下“微服务”和“分布式”这两个概念。它们都是现代软件开发中非常重要的技术，特别是在大型系统或互联网应用中经常被使用。\n\n---\n\n### 一、什么是“微服务”？\n\n**微服务（Microservices）** 是一种软件架构风格，它将一个大的应用程序拆分成多个**小型、独立的服务**，每个服务都运行在自己的进程中，并通过轻量级的通信机制（比如 HTTP 或消息队列）进行交互。\n\n#### 微服务的特点：\n\n1. **独立性**：每个服务都可以独立开发、测试、部署和扩展。\n2. **松耦合**：各个服务之间相互依赖少，修改一个服务不影响其他服务。\n3. **技术多样性**：不同的服务可以用不同的编程语言、数据库等技术实现。\n4. **可伸缩性**：可以根据需求单独扩展某个服务，而不是整个系统。\n\n#### 举个例子：\n\n想象你有一个电商网站，传统做法是一个大系统做所有事情。但用微服务的话，可以拆成：\n- 用户服务（处理登录、注册）\n- 订单服务（处理下单、支付）\n- 商品服务（管理商品信息）\n- 支付服务（处理支付）\n\n每个服务都可以独立运行和维护。\n\n---\n\n### 二、什么是“分布式”？\n\n**分布式（Distributed）** 是指将一个系统拆分成多个部分，这些部分运行在不同的计算机上，彼此之间通过网络进行通信和协调，共同完成一个任务。\n\n#### 分布式系统的特点：\n\n1. **多节点协作**：多个服务器、机器或进程协同工作。\n2. **高可用性**：即使某个节点故障，系统仍能继续运行。\n3. **负载均衡**：任务可以分配到不同节点上，提高效率。\n4. **容错性**：系统能够容忍部分失败并自动恢复。\n\n#### 举个例子：\n\n比如你访问一个大型网站，后台可能有几十台服务器同时处理请求，每台服务器负责一部分任务。这就是典型的分布式系统。\n\n---\n\n### 三、微服务和分布式有什么关系？\n\n微服务通常是在**分布式系统**的基础上构建的。也就是说，微服务是一种**基于分布式架构的设计方式**。\n\n- 每个微服务可以部署在不同的服务器上；\n- 服务之间通过网络通信；\n- 系统整体是分布式的，但每个服务是独立的。\n\n所以可以说：**微服务是分布式的一种实现方式**。\n\n---\n\n### 四、总结一下\n\n| 项目 | 微服务 | 分布式 |\n|------|--------|--------|\n| 定义 | 将系统拆分为多个独立的小服务 | 将系统拆分为多个节点，协同工作 |\n| 特点 | 独立、松耦合、可扩展 | 多节点、高可用、容错 |\n| 关系 | 微服务是分布式的一种实现方式 | 分布式是更广义的概念 |\n\n---\n\n如果你对这些概念感兴趣，我还可以推荐一些相关的课程或者学习资源哦！你想了解哪方面的内容呢？比如 Java 微服务、Spring Cloud、分布式系统设计等等？', NULL, NULL, 0, 'qwen-turbo', '2025-10-18 18:26:21');
INSERT INTO `chat_history` VALUES (21, 103, '153f9b6b-bda4-43fe-9b1c-be51c45ae755', 'user', '分析我的学习情况', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 16:05:30');
INSERT INTO `chat_history` VALUES (22, 103, '153f9b6b-bda4-43fe-9b1c-be51c45ae755', 'assistant', '你好呀！我是小途，你的学习助手。为了更好地分析你的学习情况，我需要了解一些信息。你可以告诉我以下内容吗？\n\n1. **你目前的年级**（比如小学、初中、高中、大学等）。\n2. **你最感兴趣的学科**是哪些？有没有特别薄弱的科目？\n3. **你最近的学习目标**是什么？比如考试、升学、兴趣培养等。\n4. **你平时的学习方式**是怎样的？比如自学、听课、做题、看视频等。\n5. **有没有遇到什么困难或问题**？比如时间管理、理解难点、缺乏动力等。\n\n这样我就能为你提供更有针对性的学习建议和课程推荐啦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 16:05:30');
INSERT INTO `chat_history` VALUES (23, 103, 'e0c130be-d2d2-4ee0-ac77-4d12dcca2852', 'user', '分析一下我目前的学习进度', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 16:06:24');
INSERT INTO `chat_history` VALUES (24, 103, 'e0c130be-d2d2-4ee0-ac77-4d12dcca2852', 'assistant', '你好呀！我是小途，你的学习助手。要分析你目前的学习进度，我需要一些基本信息哦。你可以告诉我以下内容吗？\n\n1. **你目前的年级**（比如小学、初中、高中、大学）  \n2. **你正在学习的科目**（如数学、语文、英语、物理等）  \n3. **你最近在学哪些知识点或章节**  \n4. **你有没有遇到什么困难或不懂的地方**  \n5. **你希望提升哪方面的能力**（比如考试成绩、理解能力、写作水平等）\n\n有了这些信息，我就能更准确地帮你分析学习进度，并给出适合你的建议和课程推荐啦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 16:06:24');
INSERT INTO `chat_history` VALUES (25, 103, 'ca28d6e2-de51-4a6c-9414-1ca74fa44bf6', 'user', '分析我的学习情况', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:44:35');
INSERT INTO `chat_history` VALUES (26, 103, 'ca28d6e2-de51-4a6c-9414-1ca74fa44bf6', 'assistant', '你好呀，我是小途！很高兴能和你一起学习。为了更好地分析你的学习情况，我需要了解一些信息。你可以告诉我：\n\n1. 你现在在哪个年级？（比如小学、初中、高中、大学等）\n2. 你最近在学习哪些科目？有没有特别喜欢或者觉得困难的科目？\n3. 你平时的学习方式是怎样的？比如：看书、听课、做题、复习笔记等\n4. 有没有什么具体的学习目标？比如考试、提高成绩、培养兴趣等\n\n只要你愿意分享，我会根据你的实际情况给出更贴合的建议和帮助哦！💪📚', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:44:35');
INSERT INTO `chat_history` VALUES (27, 103, '2f9e49c2-3900-48cd-8b50-1239dce855dd', 'user', '推荐适合我的课程', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:45:00');
INSERT INTO `chat_history` VALUES (28, 103, '2f9e49c2-3900-48cd-8b50-1239dce855dd', 'assistant', '你好呀！我是小途，很高兴为你服务。不过我还不太了解你的具体情况呢，比如：\n\n- 你目前的年级是几年级？\n- 你对哪些学科比较感兴趣或需要帮助？\n- 有没有特别想提升的方面，比如数学、语文、英语，还是其他科目？\n- 有没有特定的学习目标，比如提高成绩、准备考试，或是培养兴趣？\n\n这样我可以更精准地为你推荐适合的课程哦！你可以告诉我这些信息，我会尽力帮你找到最适合的学习方案～ 😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:45:00');
INSERT INTO `chat_history` VALUES (29, 103, '6134e43a-e651-43aa-a15d-e97d2b38d953', 'user', '分析我的学习情况', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:47:22');
INSERT INTO `chat_history` VALUES (30, 103, '6134e43a-e651-43aa-a15d-e97d2b38d953', 'assistant', '你好呀！我是小途，你的学习助手。要分析你的学习情况，我需要先了解一些基本信息。你可以告诉我：\n\n1. **你目前的年级**（比如小学、初中、高中、大学等）；\n2. **你最感兴趣或最想提升的科目**（比如数学、英语、语文、物理等）；\n3. **你在学习中遇到的主要困难**（比如时间管理、理解知识点、考试压力等）；\n4. **你平时的学习方式**（比如自学、听课、做题、看视频等）；\n5. **你有没有参加过什么课程或者辅导班**？\n\n这些信息能帮助我更好地了解你的学习情况，然后我可以为你提供更有针对性的学习建议和课程推荐哦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:47:22');
INSERT INTO `chat_history` VALUES (31, 103, '165df01a-c5fb-44ed-8e26-fcf5222abc2c', 'user', '如何成为架构师，我需要学习哪些', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:53:23');
INSERT INTO `chat_history` VALUES (32, 103, '165df01a-c5fb-44ed-8e26-fcf5222abc2c', 'assistant', '你好呀，我是小途，很高兴为你解答学习问题！你想成为架构师，这是一个非常有挑战性但也非常有成就感的职业方向。架构师是负责设计系统整体结构、保证系统稳定性、可扩展性和性能的关键角色。\n\n要成为架构师，你需要具备扎实的技术基础、良好的逻辑思维能力，以及一定的项目经验。下面我给你一个清晰的学习路径和需要掌握的知识点：\n\n---\n\n### 一、基础知识阶段（打牢根基）\n\n#### 1. 编程语言\n- **Java / Python / C# / Go** 等主流语言（根据你感兴趣的方向选择）\n- 掌握语法、数据结构、算法、面向对象编程等\n\n#### 2. 数据结构与算法\n- 学习常见的数据结构（数组、链表、树、图等）\n- 掌握排序、查找、动态规划等常用算法\n- 推荐书籍：《算法导论》、《剑指Offer》\n\n#### 3. 操作系统\n- 了解进程、线程、内存管理、文件系统等基本概念\n\n#### 4. 计算机网络\n- TCP/IP、HTTP、DNS、Socket等知识\n- 推荐书籍：《计算机网络：自顶向下方法》\n\n---\n\n### 二、进阶技术阶段（深入技术栈）\n\n#### 1. 数据库\n- SQL 和 NoSQL（如 MySQL、MongoDB、Redis）\n- 学会数据库设计、索引优化、事务处理等\n\n#### 2. 软件开发流程\n- 熟悉敏捷开发、持续集成/持续交付（CI/CD）、版本控制（Git）\n\n#### 3. 开发工具\n- IDE（如 IntelliJ IDEA、VS Code）\n- 调试工具、日志分析工具等\n\n---\n\n### 三、系统架构核心知识（成为架构师的关键）\n\n#### 1. 架构设计原则\n- 高内聚、低耦合、开闭原则、单一职责原则等\n- 分层设计、微服务、分布式系统等\n\n#### 2. 微服务架构\n- Spring Cloud、Dubbo、Kubernetes、Docker 等\n- 服务注册与发现、配置中心、网关、熔断机制等\n\n#### 3. 分布式系统\n- CAP 理论、一致性算法（如 Paxos、Raft）\n- 分布式事务、消息队列（如 Kafka、RabbitMQ）\n\n#### 4. 性能优化\n- 缓存策略、负载均衡、数据库读写分离、异步处理等\n\n#### 5. 安全与运维\n- 权限控制、加密、安全审计、DevOps 等\n\n---\n\n### 四、实践与项目经验（积累实战能力）\n\n- 参与或主导一些中大型项目的开发\n- 尝试自己设计一个小型系统（如电商系统、社交平台等）\n- 写博客、做技术分享，提升表达能力和影响力\n\n---\n\n### 五、推荐学习资源\n\n#### 在线课程：\n- Coursera / Udemy / B站 / 知乎专栏\n- 推荐课程：《架构师之路》、《高并发系统设计》、《微服务架构》\n\n#### 书籍推荐：\n- 《软件架构模式》\n- 《企业应用架构模式》\n- 《设计数据密集型应用》\n- 《程序员修炼之道》\n\n#### 技术社区：\n- GitHub、掘金、CSDN、知乎、SegmentFault\n\n---\n\n### 六、职业发展路径建议\n\n| 阶段 | 目标 | 建议 |\n|------|------|------|\n| 初级工程师 | 掌握开发技能 | 多写代码、参与项目 |\n| 中级工程师 | 独立负责模块 | 学习架构设计思想 |\n| 高级工程师 | 主导系统设计 | 熟悉架构原理和实战 |\n| 架构师 | 设计复杂系统 | 深入技术体系，提升全局视野 |\n\n---\n\n如果你告诉我你的专业背景、目前的水平和感兴趣的领域（比如 Java 后端、前端、移动端、AI 等），我可以为你定制一套更具体的学习计划哦！\n\n需要我帮你规划学习路线吗？😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:53:23');
INSERT INTO `chat_history` VALUES (33, 103, '39295040-4521-46b7-bbb7-0976dcad95e6', 'user', '这个知识点不理解', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:59:25');
INSERT INTO `chat_history` VALUES (34, 103, '39295040-4521-46b7-bbb7-0976dcad95e6', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。没关系，遇到不懂的知识点是很正常的，我们可以一起解决它！\n\n你可以告诉我具体是哪个知识点吗？比如数学、语文、英语，还是其他科目？这样我就能更准确地帮你讲解啦！如果你愿意的话，也可以把题目或者内容发给我，我会用简单易懂的方式给你解释。加油，我们一起努力！💪📚', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 17:59:25');
INSERT INTO `chat_history` VALUES (35, 103, '39295040-4521-46b7-bbb7-0976dcad95e6', 'user', '如何将一个项目的多个服务 部署 到不同服务器上，用 k8s 吗？', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 18:00:28');
INSERT INTO `chat_history` VALUES (36, 103, '39295040-4521-46b7-bbb7-0976dcad95e6', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。很高兴你愿意来学习这个内容！\n\n是的，**使用 Kubernetes（简称 k8s）可以非常方便地将一个项目的多个服务部署到不同的服务器（节点）上**。Kubernetes 是一个容器编排平台，可以帮助你管理、调度和运行容器化的应用。\n\n---\n\n### 🧩 一、为什么用 Kubernetes 来部署多个服务？\n\n1. **自动化部署与扩展**：你可以把每个服务作为一个独立的容器，k8s 会帮你自动分配到合适的服务器上运行。\n2. **高可用性**：如果某个服务器宕机，k8s 会自动把服务迁移到其他健康的服务器上。\n3. **资源优化**：可以根据服务器的负载动态分配资源，避免资源浪费。\n4. **灵活管理**：你可以为每个服务设置不同的配置、网络策略、存储等。\n\n---\n\n### 🛠️ 二、如何在 Kubernetes 中部署多个服务到不同服务器？\n\n#### ✅ 步骤 1：准备你的项目\n- 每个服务需要打包成 Docker 镜像。\n- 把这些镜像推送到一个镜像仓库（如 Docker Hub、Harbor、阿里云 ACR 等）。\n\n#### ✅ 步骤 2：创建 Kubernetes 的 Deployment 或 Pod\n- 每个服务对应一个 `Deployment` 或 `Pod`。\n- 在 `Deployment` 的 YAML 文件中，可以通过 `nodeSelector` 或 `affinity` 来指定该服务运行在哪台服务器上。\n\n##### 示例：通过 `nodeSelector` 指定服务运行的节点\n\n```yaml\napiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: service-a\nspec:\n  replicas: 1\n  selector:\n    matchLabels:\n      app: service-a\n  template:\n    metadata:\n      labels:\n        app: service-a\n    spec:\n      nodeSelector:\n        kubernetes.io/hostname: node1  # 指定运行在 node1 上\n      containers:\n      - name: service-a\n        image: your-registry/service-a:latest\n        ports:\n        - containerPort: 8080\n```\n\n> 这里 `kubernetes.io/hostname` 是 Kubernetes 中用来标识节点名称的标签。你可以通过 `kubectl get nodes` 查看所有节点的名称。\n\n---\n\n#### ✅ 步骤 3：部署到集群中\n使用 `kubectl apply -f deployment.yaml` 命令将服务部署到 Kubernetes 集群中。\n\n---\n\n### 🔄 三、进阶技巧：使用标签和选择器（Label & Selector）\n\n你可以给每台服务器打上标签（label），然后通过 `nodeSelector` 或 `affinity` 让服务运行在特定的服务器上。\n\n例如：\n\n```bash\nkubectl label nodes node1 server-type=backend\nkubectl label nodes node2 server-type=frontend\n```\n\n然后在 Deployment 中指定：\n\n```yaml\nnodeSelector:\n  server-type: backend\n```\n\n---\n\n### 📦 四、是否一定要用 Kubernetes？\n\n如果你只是想简单部署多个服务，也可以考虑以下方式：\n\n| 方式 | 适用场景 | 优点 | 缺点 |\n|------|----------|------|------|\n| 手动部署 | 小项目、单服务器 | 简单直接 | 不易维护、不支持扩展 |\n| Docker Compose | 多服务一起部署 | 快速搭建 | 无法跨多服务器 |\n| Kubernetes | 多服务、多服务器、高可用 | 强大、灵活、可扩展 | 学习成本较高 |\n\n---\n\n### 🎯 总结\n\n- **是的，可以用 Kubernetes 部署多个服务到不同服务器**。\n- 通过 `Deployment` + `nodeSelector` 或 `affinity` 可以控制服务运行在哪些服务器上。\n- 如果你刚开始接触，建议从简单的部署开始，慢慢熟悉后再使用高级功能。\n\n---\n\n如果你有具体的项目结构或者服务类型（比如前端、后端、数据库等），我可以帮你写一个更详细的部署方案哦！需要的话随时告诉我 😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 18:00:28');
INSERT INTO `chat_history` VALUES (37, 103, 'b780e28d-cc99-4a3f-a15b-2bcf4bf14c91', 'user', '如何高效学习编程', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 18:05:18');
INSERT INTO `chat_history` VALUES (38, 103, 'b780e28d-cc99-4a3f-a15b-2bcf4bf14c91', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。很高兴你对编程感兴趣！高效学习编程其实并不难，只要掌握正确的方法和技巧，就能事半功倍。下面我给你一些实用的学习建议：\n\n---\n\n### 一、明确目标，选对语言\n\n1. **确定学习目的**  \n   比如：你想做网站开发、移动应用、数据分析、人工智能，还是只是想了解编程？\n\n2. **选择适合的编程语言**  \n   - 网站开发：HTML/CSS/JavaScript（前端），PHP/Python/Java（后端）\n   - 移动应用：Swift（iOS）、Kotlin（Android）\n   - 数据分析：Python\n   - 人工智能：Python + TensorFlow/PyTorch\n\n---\n\n### 二、系统学习，打好基础\n\n1. **从基础开始**  \n   不要一开始就看高深的算法或项目，先掌握语法、变量、循环、函数、数据结构等基本概念。\n\n2. **使用优质课程资源**  \n   - 学途教育平台有系统的编程课程，适合不同水平的同学。\n   - 其他推荐：Codecademy、LeetCode、Coursera、B站、YouTube（搜索“入门编程”）\n\n3. **边学边练**  \n   学习编程最怕“只看不写”。每学一个知识点，就动手写代码试试看。\n\n---\n\n### 三、多实践，做项目\n\n1. **从小项目开始**  \n   比如：做一个计算器、网页小游戏、个人博客等。\n\n2. **参与开源项目**  \n   GitHub 上有很多开源项目，你可以参与其中，提升实战能力。\n\n3. **参加编程竞赛**  \n   如：蓝桥杯、ACM、LeetCode周赛等，锻炼逻辑思维和编码能力。\n\n---\n\n### 四、善用工具和社区\n\n1. **使用好IDE**  \n   - VS Code（轻量、插件多）\n   - PyCharm（Python）\n   - Android Studio（安卓开发）\n\n2. **加入编程社区**  \n   - CSDN、知乎、掘金、Stack Overflow、GitHub、QQ群/微信群等\n\n3. **遇到问题多问**  \n   不要怕提问，很多问题别人也遇到过，可以快速解决。\n\n---\n\n### 五、坚持是关键\n\n- 每天花30分钟到1小时练习，比偶尔突击更有效。\n- 设定每日/每周目标，比如“今天学会函数”、“本周完成一个小项目”。\n\n---\n\n### 六、推荐学习路径（以Python为例）\n\n| 阶段 | 内容 |\n|------|------|\n| 第一阶段 | 基本语法、变量、循环、条件语句 |\n| 第二阶段 | 函数、列表、字典、文件操作 |\n| 第三阶段 | 面向对象编程（OOP） |\n| 第四阶段 | 项目实战（如爬虫、数据分析、Web开发） |\n\n---\n\n如果你告诉我你目前的编程水平和兴趣方向，我可以为你定制一份更详细的学习计划哦！需要的话随时找我～ 😊\n\n祝你编程之路越走越远，早日成为大神！🚀', NULL, NULL, 0, 'qwen-turbo', '2025-10-19 18:05:18');
INSERT INTO `chat_history` VALUES (39, 103, '3b737165-fe5b-42b6-ade7-013df4e4e9e9', 'user', '推荐适合我的课程', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 17:54:39');
INSERT INTO `chat_history` VALUES (40, 103, '3b737165-fe5b-42b6-ade7-013df4e4e9e9', 'assistant', '你好呀！我是小途，学途教育的AI学习助手。为了给你推荐最适合的课程，我需要先了解一些你的信息哦：\n\n1. **你目前的年级或学习阶段**（比如小学、初中、高中、大学，或者准备考试）？\n2. **你想提升哪些科目或技能**？（比如数学、英语、编程、写作等）\n3. **你有什么特别的学习目标**？（比如提高成绩、准备升学、兴趣培养等）\n4. **你更喜欢哪种学习方式**？（比如视频课程、直播课、练习题、一对一辅导等）\n\n告诉我这些信息，我就能为你量身定制一份课程推荐啦！😊', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 17:54:40');
INSERT INTO `chat_history` VALUES (41, 103, '3b737165-fe5b-42b6-ade7-013df4e4e9e9', 'user', '视频课', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 17:54:53');
INSERT INTO `chat_history` VALUES (42, 103, '3b737165-fe5b-42b6-ade7-013df4e4e9e9', 'assistant', '太好了！视频课程是非常灵活、方便的学习方式，适合喜欢自主安排时间、喜欢反复观看和练习的同学。不过为了给你推荐更合适的视频课程，我还需要了解一些具体的信息：\n\n1. **你目前的年级或学习阶段**（比如小学、初中、高中、大学，或者准备考试）？\n2. **你想学习哪些科目或技能**？（比如语文、数学、英语、编程、绘画、音乐等）\n3. **你希望视频课程的内容是基础巩固、提升拔高，还是专项突破**？\n4. **有没有特别喜欢的老师风格或课程形式**？（比如讲解清晰、幽默风趣、逻辑严谨等）\n\n告诉我这些信息，我可以为你推荐最适合的视频课程哦！🌟', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 17:54:53');
INSERT INTO `chat_history` VALUES (43, 103, '680dbe0c-a5d1-4172-8661-55bbe280453f', 'user', '我想成为Java后端工程师，该怎么学习', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 18:29:32');
INSERT INTO `chat_history` VALUES (44, 103, '680dbe0c-a5d1-4172-8661-55bbe280453f', 'assistant', '你好呀！我是小途，很高兴你对Java后端工程师这个方向感兴趣。成为Java后端工程师是一个非常有前景的职业选择，而且Java在企业级开发中应用广泛，学习路径也比较清晰。下面我来为你梳理一下学习路线和建议：\n\n---\n\n### 一、基础阶段：掌握Java核心知识\n\n#### 1. **Java基础语法**\n- 数据类型、变量、运算符\n- 流程控制（if/else、for/while）\n- 数组、字符串处理\n- 面向对象编程（类、对象、继承、多态、封装、抽象类、接口）\n\n> 推荐学习方式：可以看《Java核心技术 卷I》或《Effective Java》，配合在线课程如B站、慕课网等。\n\n#### 2. **Java高级特性**\n- 集合框架（List、Set、Map等）\n- 异常处理（try/catch/finally、自定义异常）\n- 多线程与并发（Thread、Runnable、synchronized、Lock、线程池）\n- IO流（字节流、字符流、NIO）\n\n---\n\n### 二、进阶阶段：深入理解Java后端技术栈\n\n#### 1. **数据库相关**\n- SQL语言（增删改查、连接查询、事务等）\n- MySQL、PostgreSQL等常用数据库\n- JDBC（Java数据库连接）\n- ORM框架（如MyBatis、Hibernate）\n\n#### 2. **Web开发基础**\n- Servlet、JSP\n- HTTP协议、RESTful API\n- Spring框架（Spring MVC、Spring Boot）\n- Spring Data JPA / MyBatis\n\n#### 3. **中间件与工具**\n- Maven / Gradle（项目构建）\n- Git版本控制\n- Redis（缓存）\n- RabbitMQ / Kafka（消息队列）\n- Nginx（反向代理）\n\n---\n\n### 三、实战阶段：项目开发经验\n\n#### 1. **做几个真实项目**\n- 比如博客系统、电商系统、在线考试系统等\n- 尝试使用Spring Boot快速搭建项目\n- 学会部署（Docker、Linux服务器等）\n\n#### 2. **参与开源项目**\n- GitHub上找一些开源的Java项目参与\n- 提交PR、学习他人代码风格和设计思路\n\n#### 3. **准备面试**\n- 熟悉常见算法题（LeetCode、牛客网）\n- 掌握Java高频面试题（比如JVM、多线程、设计模式等）\n\n---\n\n### 四、推荐学习资源\n\n| 类型 | 推荐内容 |\n|------|----------|\n| 视频课程 | B站、慕课网、网易云课堂、极客时间 |\n| 书籍 | 《Java编程思想》、《Java并发编程实战》、《Spring实战》 |\n| 在线练习 | LeetCode、牛客网、PTA |\n| 社区 | CSDN、掘金、知乎、Stack Overflow |\n\n---\n\n### 五、学习建议\n\n- **循序渐进**：不要一开始就追求“全栈”，打好Java基础更重要。\n- **坚持实践**：学完一个知识点后，一定要动手写代码。\n- **多交流**：加入技术社区、论坛，遇到问题及时提问。\n- **持续学习**：技术更新快，保持学习热情很重要！\n\n---\n\n如果你告诉我你目前的学习水平（比如是零基础还是有一定编程基础），我可以为你量身定制一份学习计划哦！🌟\n\n需要我帮你推荐一些适合初学者的课程吗？', NULL, NULL, 0, 'qwen-turbo', '2025-10-26 18:29:32');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 1, '这门课程很棒，讲解得很详细！', 5, '2025-08-13 22:40:53', '2025-10-05 15:49:10', 0, 1, 0, 0);
INSERT INTO `comment` VALUES (2, 1, 1, '这门课程很棒，讲解得很详细！测试评论', 5, '2025-10-05 15:47:25', '2025-10-05 15:53:49', 0, 0, 0, 0);
INSERT INTO `comment` VALUES (3, 103, 1, '测试数据', 3, '2025-10-18 16:34:49', '2025-10-18 16:34:49', 0, 1, 0, 0);
INSERT INTO `comment` VALUES (4, 103, 2, '测试2222', 5, '2025-10-18 16:35:29', '2025-10-18 16:35:29', 0, 1, 0, 0);
INSERT INTO `comment` VALUES (5, 103, 5, '非常棒', 5, '2025-10-19 16:04:24', '2025-10-19 16:04:24', 0, 1, 0, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment_like
-- ----------------------------
INSERT INTO `comment_like` VALUES (2, 1, 1, 2, '2025-10-05 15:51:52', '2025-10-05 15:51:52');

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java零基础入门教程', '从零开始学习Java编程语言，包含语法基础、面向对象编程、异常处理等核心知识点。适合编程初学者，通过大量实战项目快速掌握Java开发技能。', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, 199.00, 1, 10, 0, 1, 1200, 1570, '2025-08-13 21:07:18', '2025-10-18 17:19:25');
INSERT INTO `course` VALUES (2, 'Java进阶：Spring Boot实战', '深入学习Spring Boot框架，从入门到精通。包含自动配置、起步依赖、Actuator监控、部署等高级特性。通过实际项目开发掌握企业级开发技能。', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, 299.00, 1, 10, 1, 1, 1800, 856, '2025-08-13 21:07:18', '2025-10-18 17:21:05');
INSERT INTO `course` VALUES (3, 'Java微服务架构实战', '学习微服务架构设计与实现，包含Spring Cloud、Docker、Kubernetes等技术栈。通过大型项目实战，掌握分布式系统开发与部署。', 'http://8.141.106.92/media/covers/微服务.png', 299.00, 399.00, 1, 10, 2, 1, 2400, 423, '2025-08-13 21:07:18', '2025-10-18 17:18:26');
INSERT INTO `course` VALUES (4, 'Python数据分析入门', '学习Python数据分析的核心库：NumPy、Pandas、Matplotlib。从数据清洗到可视化，掌握数据分析的完整流程。', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, 249.00, 2, 11, 0, 1, 1500, 732, '2025-08-13 21:07:18', '2025-10-18 17:23:46');
INSERT INTO `course` VALUES (5, 'Python机器学习实战', '深入学习机器学习算法与实现，包含监督学习、无监督学习、深度学习等。使用scikit-learn、TensorFlow等主流框架。', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, 599.00, 2, 11, 2, 1, 3000, 289, '2025-08-13 21:07:18', '2025-10-18 17:25:32');
INSERT INTO `course` VALUES (6, 'Vue.js 3.0 全家桶开发', '学习Vue.js 3.0的新特性，包含Composition API、Vite构建工具、Vue Router、Vuex状态管理等。通过实战项目掌握现代前端开发。', 'http://8.141.106.92/media/covers/Vue.js 3.0 全家桶开发.png', 179.00, 279.00, 3, 20, 1, 1, 1600, 945, '2025-08-13 21:07:18', '2025-10-18 17:26:58');
INSERT INTO `course` VALUES (7, 'React企业级开发实战', '深入学习React生态系统，包含Hooks、Redux、React Router、Next.js等。通过大型SPA项目掌握企业级前端开发技能。', 'http://8.141.106.92/media/covers/React企业级开发实战.png', 229.00, 329.00, 3, 21, 1, 1, 2000, 567, '2025-08-13 21:07:18', '2025-10-18 17:28:20');
INSERT INTO `course` VALUES (8, 'MySQL数据库设计与优化', '系统学习MySQL数据库，从基础语法到高级优化。包含索引设计、查询优化、存储过程、主从复制等企业级技能。', 'http://8.141.106.92/media/covers/MySQL数据库设计与优化.jpg', 129.00, 199.00, 4, 30, 0, 1, 1000, 1234, '2025-08-13 21:07:18', '2025-10-18 17:29:23');
INSERT INTO `course` VALUES (9, 'Redis缓存技术实战', '深入学习Redis缓存技术，包含数据类型、持久化、集群、哨兵等高级特性。掌握高并发系统的缓存设计与优化。', 'http://8.141.106.92/media/covers/Redis缓存技术实战.png', 199.00, 299.00, 4, 31, 1, 1, 1200, 678, '2025-08-13 21:07:18', '2025-10-18 17:30:21');
INSERT INTO `course` VALUES (10, 'Kubernetes容器编排', '学习Kubernetes容器编排技术，包含Pod、Service、Deployment等核心概念。掌握云原生应用的部署与管理。', 'http://8.141.106.92/media/covers/Kubernetes容器编排.png', 349.00, 449.00, 5, 10, 2, 1, 2200, 0, '2025-08-13 21:07:18', '2025-10-18 17:40:41');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程标签关联表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of learning_record
-- ----------------------------
INSERT INTO `learning_record` VALUES (1, 0, 0, 0, 0, '2025-09-07 17:07:37', '2025-09-07 17:07:37', '2025-09-07 17:07:37');
INSERT INTO `learning_record` VALUES (1, 1, 1, 900, 0, '2025-09-09 18:24:09', '2025-01-16 10:35:00', '2025-09-09 18:24:09');
INSERT INTO `learning_record` VALUES (1, 1, 2, 1500, 1, '2025-09-09 18:23:45', '2025-01-16 10:35:00', '2025-09-09 18:23:45');
INSERT INTO `learning_record` VALUES (1, 1, 3, 1200, 1, '2025-01-16 11:00:00', '2025-01-16 10:35:00', '2025-01-16 11:00:00');
INSERT INTO `learning_record` VALUES (1, 1, 4, 1200, 0, '2025-01-16 11:15:00', '2025-01-16 10:35:00', '2025-01-16 11:15:00');
INSERT INTO `learning_record` VALUES (1, 1, 10, 800, 0, '2025-01-16 14:20:00', '2025-01-16 10:35:00', '2025-01-16 14:20:00');
INSERT INTO `learning_record` VALUES (1, 2, 20, 1200, 1, '2025-01-17 09:30:00', '2025-01-17 09:30:00', '2025-01-17 09:30:00');
INSERT INTO `learning_record` VALUES (1, 2, 21, 1800, 0, '2025-01-17 10:00:00', '2025-01-17 09:30:00', '2025-01-17 10:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 1, 900, 1, '2025-01-15 09:25:00', '2025-01-15 09:25:00', '2025-01-15 09:25:00');
INSERT INTO `learning_record` VALUES (2, 1, 2, 1500, 1, '2025-01-15 09:40:00', '2025-01-15 09:25:00', '2025-01-15 09:40:00');
INSERT INTO `learning_record` VALUES (2, 1, 3, 1200, 1, '2025-01-15 10:00:00', '2025-01-15 09:25:00', '2025-01-15 10:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 4, 1800, 1, '2025-01-15 10:30:00', '2025-01-15 09:25:00', '2025-01-15 10:30:00');
INSERT INTO `learning_record` VALUES (2, 1, 10, 2100, 1, '2025-01-15 11:00:00', '2025-01-15 09:25:00', '2025-01-15 11:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 11, 1500, 1, '2025-01-15 11:30:00', '2025-01-15 09:25:00', '2025-01-15 11:30:00');
INSERT INTO `learning_record` VALUES (2, 1, 12, 2400, 1, '2025-01-15 12:00:00', '2025-01-15 09:25:00', '2025-01-15 12:00:00');
INSERT INTO `learning_record` VALUES (2, 1, 13, 1800, 0, '2025-01-15 14:30:00', '2025-01-15 09:25:00', '2025-01-15 14:30:00');
INSERT INTO `learning_record` VALUES (103, 1, 1, 36100, 0, '2025-10-26 18:27:08', '2025-10-15 19:26:01', '2025-10-26 18:27:08');
INSERT INTO `learning_record` VALUES (103, 1, 2, 100, 0, '2025-10-18 19:25:54', '2025-10-15 19:21:19', '2025-10-18 19:25:54');
INSERT INTO `learning_record` VALUES (103, 1, 3, 69, 0, '2025-10-18 19:20:01', '2025-10-18 18:37:22', '2025-10-18 19:20:01');
INSERT INTO `learning_record` VALUES (103, 2, 20, 99, 0, '2025-10-19 18:42:59', '2025-10-18 19:15:36', '2025-10-19 18:42:59');
INSERT INTO `learning_record` VALUES (103, 2, 21, 98, 0, '2025-10-19 18:43:12', '2025-10-19 18:43:00', '2025-10-19 18:43:12');
INSERT INTO `learning_record` VALUES (103, 2, 22, 96, 0, '2025-10-19 18:43:25', '2025-10-19 18:43:14', '2025-10-19 18:43:25');
INSERT INTO `learning_record` VALUES (103, 1, 23, 32, 0, '2025-10-18 19:14:31', '2025-10-18 16:00:24', '2025-10-18 19:14:31');
INSERT INTO `learning_record` VALUES (103, 4, 41, 1, 0, '2025-10-26 18:43:06', '2025-10-26 18:43:06', '2025-10-26 18:43:06');
INSERT INTO `learning_record` VALUES (103, 2, 150, 98, 0, '2025-10-19 18:43:36', '2025-10-19 18:43:27', '2025-10-19 18:43:36');

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
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课时表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lesson
-- ----------------------------
INSERT INTO `lesson` VALUES (1, 1, '1.1 测试数据', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 1, 1, '2025-08-13 21:07:18', '2025-10-18 18:36:06');
INSERT INTO `lesson` VALUES (2, 1, '1.2 JDK安装与配置', 'http://8.141.106.92/media/videos/Java0.mp4', 25, 1, 2, '2025-08-13 21:07:18', '2025-10-18 18:36:10');
INSERT INTO `lesson` VALUES (3, 1, '1.3 开发工具IDEA安装', 'http://8.141.106.92/media/videos/Java0.mp4', 20, 1, 3, '2025-08-13 21:07:18', '2025-10-18 18:36:20');
INSERT INTO `lesson` VALUES (4, 1, '1.4 第一个Java程序', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 0, 4, '2025-08-13 21:07:18', '2025-10-18 18:36:23');
INSERT INTO `lesson` VALUES (11, 2, '2.2 变量与常量', 'http://8.141.106.92/media/videos/Java0.mp4', 25, 0, 2, '2025-08-13 21:07:18', '2025-10-18 19:42:28');
INSERT INTO `lesson` VALUES (12, 2, '2.3 运算符与表达式', 'http://8.141.106.92/media/videos/Java0.mp4', 40, 0, 3, '2025-08-13 21:07:18', '2025-10-18 19:42:31');
INSERT INTO `lesson` VALUES (13, 2, '2.4 控制流程语句', 'http://8.141.106.92/media/videos/Java0.mp4', 45, 0, 4, '2025-08-13 21:07:18', '2025-10-18 19:42:37');
INSERT INTO `lesson` VALUES (20, 10, '1.1 Spring Boot简介', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 20, 1, 1, '2025-08-13 21:07:18', '2025-10-19 15:47:33');
INSERT INTO `lesson` VALUES (21, 10, '1.2 创建第一个Spring Boot项目', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 1, 2, '2025-08-13 21:07:18', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (22, 10, '1.3 项目结构分析', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 25, 0, 3, '2025-08-13 21:07:18', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (23, 1, '1.1 Java环境搭建test', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 1, 1, '2025-10-05 16:13:59', '2025-10-18 19:42:06');
INSERT INTO `lesson` VALUES (24, 30, '1.1 微服务架构概述', 'http://8.141.106.92/media/videos/springcloud.mp4', 25, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (25, 30, '1.2 单体应用vs微服务', 'http://8.141.106.92/media/videos/springcloud.mp4', 30, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (26, 30, '1.3 微服务设计原则', 'http://8.141.106.92/media/videos/springcloud.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (27, 30, '1.4 服务拆分策略', 'http://8.141.106.92/media/videos/springcloud.mp4', 40, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (28, 31, '2.1 Spring Cloud简介', 'http://8.141.106.92/media/videos/springcloud.mp4', 20, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (29, 31, '2.2 Eureka服务注册中心', 'http://8.141.106.92/media/videos/springcloud.mp4', 45, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (30, 31, '2.3 Ribbon负载均衡', 'http://8.141.106.92/media/videos/springcloud.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (31, 31, '2.4 Feign声明式服务调用', 'http://8.141.106.92/media/videos/springcloud.mp4', 40, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (32, 32, '3.1 Nacos配置中心', 'http://8.141.106.92/media/videos/springcloud.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (33, 32, '3.2 Gateway网关配置', 'http://8.141.106.92/media/videos/springcloud.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (34, 32, '3.3 Sentinel流量控制', 'http://8.141.106.92/media/videos/springcloud.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (35, 33, '4.1 Docker基础入门', 'http://8.141.106.92/media/videos/springcloud.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (36, 33, '4.2 编写Dockerfile', 'http://8.141.106.92/media/videos/springcloud.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (37, 33, '4.3 Docker Compose多容器部署', 'http://8.141.106.92/media/videos/springcloud.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (38, 34, '5.1 K8s架构原理', 'http://8.141.106.92/media/videos/springcloud.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (39, 34, '5.2 部署Spring Cloud到K8s', 'http://8.141.106.92/media/videos/springcloud.mp4', 50, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (40, 34, '5.3 监控与日志管理', 'http://8.141.106.92/media/videos/springcloud.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (41, 40, '1.1 数据分析入门', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 20, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (42, 40, '1.2 Jupyter Notebook使用', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 25, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (43, 40, '1.3 数据分析流程', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 30, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (44, 41, '2.1 NumPy数组创建', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (45, 41, '2.2 数组索引与切片', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 30, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (46, 41, '2.3 数组运算与统计', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (47, 42, '3.1 Pandas数据结构', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (48, 42, '3.2 数据清洗技巧', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 45, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (49, 42, '3.3 数据聚合与分组', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (50, 43, '4.1 Matplotlib绘图基础', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (51, 43, '4.2 统计图表绘制', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (52, 43, '4.3 综合案例实战', 'http://8.141.106.92/media/videos/Pythonshujufenxi.mp4', 50, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (53, 50, '1.1 机器学习概述', 'http://8.141.106.92/media/videos/python.mp4', 30, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (54, 50, '1.2 环境搭建与工具', 'http://8.141.106.92/media/videos/python.mp4', 25, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (55, 50, '1.3 特征工程基础', 'http://8.141.106.92/media/videos/python.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (56, 51, '2.1 线性回归算法', 'http://8.141.106.92/media/videos/python.mp4', 45, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (57, 51, '2.2 逻辑回归分类', 'http://8.141.106.92/media/videos/python.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (58, 51, '2.3 决策树与随机森林', 'http://8.141.106.92/media/videos/python.mp4', 50, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (59, 51, '2.4 支持向量机SVM', 'http://8.141.106.92/media/videos/python.mp4', 45, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (60, 52, '3.1 聚类算法K-Means', 'http://8.141.106.92/media/videos/python.mp4', 40, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (61, 52, '3.2 降维算法PCA', 'http://8.141.106.92/media/videos/python.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (62, 53, '4.1 神经网络基础', 'http://8.141.106.92/media/videos/python.mp4', 45, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (63, 53, '4.2 TensorFlow入门', 'http://8.141.106.92/media/videos/python.mp4', 50, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (64, 53, '4.3 卷积神经网络CNN', 'http://8.141.106.92/media/videos/python.mp4', 55, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (65, 54, '5.1 图像分类项目', 'http://8.141.106.92/media/videos/python.mp4', 60, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (66, 54, '5.2 推荐系统实战', 'http://8.141.106.92/media/videos/python.mp4', 65, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (67, 70, '1.1 React核心概念', 'http://8.141.106.92/media/videos/react.mp4', 25, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (68, 70, '1.2 JSX语法详解', 'http://8.141.106.92/media/videos/react.mp4', 30, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (69, 70, '1.3 组件与Props', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (70, 71, '2.1 useState Hook', 'http://8.141.106.92/media/videos/react.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (71, 71, '2.2 useEffect Hook', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (72, 71, '2.3 useContext Hook', 'http://8.141.106.92/media/videos/react.mp4', 30, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (73, 71, '2.4 自定义Hooks', 'http://8.141.106.92/media/videos/react.mp4', 40, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (74, 72, '3.1 Redux核心概念', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (75, 72, '3.2 Redux Toolkit', 'http://8.141.106.92/media/videos/react.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (76, 72, '3.3 Redux中间件', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (77, 73, '4.1 React Router配置', 'http://8.141.106.92/media/videos/react.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (78, 73, '4.2 路由嵌套与参数', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (79, 73, '4.3 路由守卫与懒加载', 'http://8.141.106.92/media/videos/react.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (80, 74, '5.1 项目架构设计', 'http://8.141.106.92/media/videos/react.mp4', 45, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (81, 74, '5.2 组件库封装', 'http://8.141.106.92/media/videos/react.mp4', 50, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (82, 74, '5.3 性能优化技巧', 'http://8.141.106.92/media/videos/react.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (83, 74, '5.4 项目打包部署', 'http://8.141.106.92/media/videos/react.mp4', 35, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (84, 80, '1.1 MySQL安装与配置', 'http://8.141.106.92/media/videos/Mysql.mp4', 20, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (85, 80, '1.2 SQL基础语法', 'http://8.141.106.92/media/videos/Mysql.mp4', 30, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (86, 80, '1.3 数据类型详解', 'http://8.141.106.92/media/videos/Mysql.mp4', 25, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (87, 81, '2.1 数据库设计原则', 'http://8.141.106.92/media/videos/Mysql.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (88, 81, '2.2 ER图与建模', 'http://8.141.106.92/media/videos/Mysql.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (89, 81, '2.3 三范式设计', 'http://8.141.106.92/media/videos/Mysql.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (90, 82, '3.1 索引原理与类型', 'http://8.141.106.92/media/videos/Mysql.mp4', 40, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (91, 82, '3.2 查询优化技巧', 'http://8.141.106.92/media/videos/Mysql.mp4', 45, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (92, 82, '3.3 Explain执行计划分析', 'http://8.141.106.92/media/videos/Mysql.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (93, 83, '4.1 存储过程与函数', 'http://8.141.106.92/media/videos/Mysql.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (94, 83, '4.2 事务与锁机制', 'http://8.141.106.92/media/videos/Mysql.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (95, 83, '4.3 主从复制配置', 'http://8.141.106.92/media/videos/Mysql.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (96, 91, '1.1 Redis简介与安装', 'http://8.141.106.92/media/videos/redis.mp4', 20, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (97, 90, '1.2 Redis基本命令', 'http://8.141.106.92/media/videos/redis.mp4', 25, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (98, 90, '1.3 Redis客户端工具', 'http://8.141.106.92/media/videos/redis.mp4', 20, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (99, 91, '2.1 String字符串类型', 'http://8.141.106.92/media/videos/redis.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (100, 91, '2.2 Hash哈希类型', 'http://8.141.106.92/media/videos/redis.mp4', 30, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (101, 91, '2.3 List列表类型', 'http://8.141.106.92/media/videos/redis.mp4', 30, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (102, 91, '2.4 Set集合与ZSet', 'http://8.141.106.92/media/videos/redis.mp4', 35, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (103, 92, '3.1 持久化RDB与AOF', 'http://8.141.106.92/media/videos/redis.mp4', 40, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (104, 92, '3.2 主从复制', 'http://8.141.106.92/media/videos/redis.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (105, 92, '3.3 哨兵模式', 'http://8.141.106.92/media/videos/redis.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (106, 92, '3.4 集群模式', 'http://8.141.106.92/media/videos/redis.mp4', 45, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (107, 93, '4.1 缓存穿透解决方案', 'http://8.141.106.92/media/videos/redis.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (108, 93, '4.2 缓存雪崩与击穿', 'http://8.141.106.92/media/videos/redis.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (109, 93, '4.3 分布式锁实现', 'http://8.141.106.92/media/videos/redis.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (110, 100, '1.1 Kubernetes概述', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 25, 1, 1, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (111, 100, '1.2 环境搭建', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 30, 1, 2, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (112, 100, '1.3 架构与组件', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (113, 101, '2.1 Pod详解', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 40, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (114, 101, '2.2 Service服务', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (115, 101, '2.3 Deployment部署', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (116, 101, '2.4 ConfigMap与Secret', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 35, 0, 4, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (117, 102, '3.1 部署有状态应用', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 45, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (118, 102, '3.2 Ingress网关配置', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (119, 102, '3.3 持久化存储', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 45, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (120, 103, '4.1 水平扩展HPA', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (121, 103, '4.2 监控与日志', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (122, 103, '4.3 CI/CD流水线', 'http://8.141.106.92/media/videos/Kubernetes.mp4', 50, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:00:32');
INSERT INTO `lesson` VALUES (123, 2, '2.1 数据类型详解', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (124, 20, '1.2 Vue3环境搭建', 'http://8.141.106.92/media/videos/Vue.mp4', 25, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (125, 20, '1.3 创建第一个Vue3项目', 'http://8.141.106.92/media/videos/Vue.mp4', 30, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (126, 21, '2.1 setup函数详解', 'http://8.141.106.92/media/videos/Vue.mp4', 35, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (127, 21, '2.2 ref与reactive', 'http://8.141.106.92/media/videos/Vue.mp4', 30, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (128, 21, '2.3 computed与watch', 'http://8.141.106.92/media/videos/Vue.mp4', 35, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (129, 22, '3.1 组件通信方式', 'http://8.141.106.92/media/videos/Vue.mp4', 40, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (130, 22, '3.2 插槽与动态组件', 'http://8.141.106.92/media/videos/Vue.mp4', 35, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (131, 22, '3.3 组件最佳实践', 'http://8.141.106.92/media/videos/Vue.mp4', 40, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (132, 23, '4.1 Vue Router配置', 'http://8.141.106.92/media/videos/Vue.mp4', 30, 0, 1, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (133, 23, '4.2 Pinia状态管理', 'http://8.141.106.92/media/videos/Vue.mp4', 40, 0, 2, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (134, 23, '4.3 综合项目实战', 'http://8.141.106.92/media/videos/Vue.mp4', 50, 0, 3, '2025-10-18 17:40:41', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (135, 3, '3.1 类与对象', 'http://8.141.106.92/media/videos/Java0.mp4', 35, 0, 1, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (136, 3, '3.2 封装与访问控制', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 0, 2, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (137, 3, '3.3 继承与多态', 'http://8.141.106.92/media/videos/Java0.mp4', 40, 0, 3, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (138, 3, '3.4 抽象类与接口', 'http://8.141.106.92/media/videos/Java0.mp4', 35, 0, 4, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (139, 3, '3.5 面向对象综合案例', 'http://8.141.106.92/media/videos/Java0.mp4', 45, 0, 5, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (140, 4, '4.1 异常机制概述', 'http://8.141.106.92/media/videos/Java0.mp4', 25, 0, 1, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (141, 4, '4.2 try-catch-finally', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 0, 2, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (142, 4, '4.3 自定义异常', 'http://8.141.106.92/media/videos/Java0.mp4', 35, 0, 3, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (143, 4, '4.4 调试技巧与工具', 'http://8.141.106.92/media/videos/Java0.mp4', 40, 0, 4, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (144, 5, '5.1 集合框架概述', 'http://8.141.106.92/media/videos/Java0.mp4', 30, 0, 1, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (145, 5, '5.2 List接口与实现类', 'http://8.141.106.92/media/videos/Java0.mp4', 40, 0, 2, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (146, 5, '5.3 Set接口与实现类', 'http://8.141.106.92/media/videos/Java0.mp4', 35, 0, 3, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (147, 5, '5.4 Map接口与实现类', 'http://8.141.106.92/media/videos/Java0.mp4', 40, 0, 4, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (148, 5, '5.5 集合工具类与泛型', 'http://8.141.106.92/media/videos/Java0.mp4', 35, 0, 5, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (149, 5, '5.6 集合框架综合案例', 'http://8.141.106.92/media/videos/Java0.mp4', 45, 0, 6, '2025-10-18 17:50:16', '2025-10-19 15:59:27');
INSERT INTO `lesson` VALUES (150, 11, '2.1 Spring Boot自动配置机制', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 1, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (151, 11, '2.2 条件注解详解', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 30, 0, 2, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (152, 11, '2.3 自定义Starter', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 3, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (153, 11, '2.4 配置文件详解', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 4, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (154, 12, '3.1 RESTful API设计', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 1, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (155, 12, '3.2 Controller层开发', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 2, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (156, 12, '3.3 参数接收与验证', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 3, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (157, 12, '3.4 统一异常处理', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 30, 0, 4, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (158, 12, '3.5 文件上传下载', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 5, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (159, 12, '3.6 Swagger接口文档', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 30, 0, 6, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (160, 13, '4.1 Spring Data JPA入门', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 1, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (161, 13, '4.2 JPA查询方法', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 2, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (162, 13, '4.3 MyBatis整合', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 45, 0, 3, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (163, 13, '4.4 MyBatis-Plus实战', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 4, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (164, 13, '4.5 多数据源配置', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 5, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (165, 13, '4.6 事务管理', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 6, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (166, 14, '5.1 Spring Security入门', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 40, 0, 1, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (167, 14, '5.2 JWT令牌认证', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 45, 0, 2, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (168, 14, '5.3 权限控制', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 3, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (169, 14, '5.4 Actuator监控', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 30, 0, 4, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (170, 14, '5.5 日志管理', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 30, 0, 5, '2025-10-18 17:50:16', '2025-10-19 16:09:46');
INSERT INTO `lesson` VALUES (171, 14, '5.6 应用部署与打包', 'http://8.141.106.92/media/videos/SpringBoot.mp4', 35, 0, 6, '2025-10-18 17:50:16', '2025-10-19 16:09:46');

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
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note
-- ----------------------------
INSERT INTO `note` VALUES (2, 1, 1, 2, 'JDK安装要点', 'JDK安装过程中需要注意的几个要点：\n1. 选择合适的版本（推荐JDK 17）\n2. 配置环境变量JAVA_HOME\n3. 配置PATH路径\n4. 验证安装：java -version', '2025-01-16 10:50:00', '2025-01-16 10:50:00');
INSERT INTO `note` VALUES (3, 1, 1, 3, 'IDEA使用技巧', 'IntelliJ IDEA常用快捷键：\n- Ctrl+Space：代码补全\n- Ctrl+D：复制当前行\n- Ctrl+Y：删除当前行\n- Ctrl+/：注释/取消注释\n- Shift+F10：运行程序', '2025-01-16 11:05:00', '2025-01-16 11:05:00');
INSERT INTO `note` VALUES (4, 1, 1, 4, '第一个Java程序总结', 'Hello World程序要点：\n1. 类名必须与文件名一致\n2. main方法是程序入口\n3. System.out.println()用于输出\n4. 每个语句以分号结尾\n\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}', '2025-01-16 11:20:00', '2025-01-16 11:20:00');
INSERT INTO `note` VALUES (5, 1, 1, 10, 'Java数据类型笔记', 'Java基本数据类型：\n1. 整数类型：byte, short, int, long\n2. 浮点类型：float, double\n3. 字符类型：char\n4. 布尔类型：boolean\n\n引用数据类型：\n- 类(Class)\n- 接口(Interface)\n- 数组(Array)', '2025-01-16 14:25:00', '2025-01-16 14:25:00');
INSERT INTO `note` VALUES (6, 1, 2, 20, 'Spring Boot简介笔记', 'Spring Boot的优势：\n1. 自动配置：减少样板代码\n2. 起步依赖：简化依赖管理\n3. 内嵌服务器：无需外部容器\n4. 生产就绪：提供监控、健康检查等功能\n\n核心注解：\n@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan', '2025-01-17 09:35:00', '2025-01-17 09:35:00');
INSERT INTO `note` VALUES (7, 1, 2, 21, '创建Spring Boot项目', '创建Spring Boot项目的方式：\n1. 使用Spring Initializr（推荐）\n2. 使用IDE创建\n3. 使用Maven/Gradle手动创建\n\n项目结构：\nsrc/main/java - Java源码\nsrc/main/resources - 配置文件\nsrc/test/java - 测试代码', '2025-01-17 10:05:00', '2025-01-17 10:05:00');
INSERT INTO `note` VALUES (8, 2, 1, 1, 'Java学习开始', '开始Java编程之旅！作为一个编程新手，我要认真学习每一个知识点。这门课程看起来很系统，期待能够掌握Java编程技能。', '2025-01-15 09:30:00', '2025-01-15 09:30:00');
INSERT INTO `note` VALUES (9, 2, 1, 2, 'JDK环境配置', '今天学会了JDK的安装和配置，终于可以运行Java程序了！\n重要步骤：\n1. 下载JDK\n2. 安装到指定目录\n3. 设置JAVA_HOME\n4. 添加bin目录到PATH\n测试命令：java -version', '2025-01-15 09:45:00', '2025-01-15 09:45:00');
INSERT INTO `note` VALUES (10, 2, 1, 4, '第一个程序运行成功', '太激动了！第一个Java程序运行成功了！\n虽然只是简单的Hello World，但这是我编程路上的第一步。\n接下来要继续学习更多的Java语法知识。', '2025-01-15 10:35:00', '2025-01-15 10:35:00');
INSERT INTO `note` VALUES (11, 2, 1, 10, 'Java数据类型学习心得', '今天学习了Java的数据类型，感觉内容还是挺多的：\n\n基本类型记忆口诀：\n- 整数：byte short int long（从小到大）\n- 小数：float double（单精度双精度）\n- 字符：char（单个字符）\n- 布尔：boolean（true/false）\n\n需要多练习才能熟练掌握！', '2025-01-15 11:05:00', '2025-01-15 11:05:00');
INSERT INTO `note` VALUES (12, 2, 1, 11, '变量与常量', '变量与常量的区别：\n变量：可以改变值的量\n常量：不能改变值的量（用final修饰）\n\n变量命名规则：\n1. 只能包含字母、数字、下划线、$\n2. 不能以数字开头\n3. 不能使用关键字\n4. 建议使用驼峰命名法', '2025-01-15 11:35:00', '2025-01-15 11:35:00');
INSERT INTO `note` VALUES (13, 2, 1, 12, '运算符总结', 'Java运算符分类：\n1. 算术运算符：+, -, *, /, %\n2. 赋值运算符：=, +=, -=, *=, /=\n3. 比较运算符：==, !=, >, <, >=, <=\n4. 逻辑运算符：&&, ||, !\n5. 位运算符：&, |, ^, ~, <<, >>\n\n注意：++和--的前置与后置区别！', '2025-01-15 12:05:00', '2025-01-15 12:05:00');
INSERT INTO `note` VALUES (14, 2, 1, 13, '控制流程语句重点', '控制流程语句是编程的核心：\n\n1. 条件语句：\n   - if...else\n   - switch...case\n\n2. 循环语句：\n   - for循环\n   - while循环\n   - do...while循环\n\n3. 跳转语句：\n   - break：跳出循环\n   - continue：跳过本次循环\n   - return：返回方法\n\n需要多写代码练习！', '2025-01-15 14:35:00', '2025-01-15 14:35:00');
INSERT INTO `note` VALUES (15, 1, 1, 1, '一次测试', '测试接口', '2025-09-09 18:37:52', '2025-09-09 18:37:52');
INSERT INTO `note` VALUES (16, 1, 1, 1, '一次测试', '测试接口', '2025-09-09 18:41:09', '2025-09-09 18:41:09');
INSERT INTO `note` VALUES (17, 1, 1, 1, '一次测试', '测试接口', '2025-09-09 18:41:36', '2025-09-09 18:41:36');
INSERT INTO `note` VALUES (18, 103, 1, 1, 'Java学习', '第一次测试笔记功能，测试成功。。。。。', '2025-10-22 17:45:11', '2025-10-22 18:01:37');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `actual_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实际支付金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '订单状态(0:待支付,1:已支付,2:已取消,3:已退款,4:已完成)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (2, 'ORD202510051722210003', 1, 548.00, 548.00, 0.00, 0, '购买Java课程', NULL, '2025-10-05 17:22:21', '2025-10-05 17:22:21');
INSERT INTO `order` VALUES (3, 'ORD202510141841310001', 103, 99.00, 99.00, 0.00, 2, NULL, NULL, '2025-10-14 18:41:32', '2025-10-14 18:41:32');
INSERT INTO `order` VALUES (4, 'ORD202510141847130002', 103, 846.00, 846.00, 0.00, 2, NULL, NULL, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order` VALUES (5, 'ORD202510141850010003', 103, 846.00, 846.00, 0.00, 2, NULL, NULL, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order` VALUES (6, 'ORD202510141855500004', 103, 846.00, 846.00, 0.00, 2, NULL, NULL, '2025-10-14 18:55:51', '2025-10-14 18:55:51');
INSERT INTO `order` VALUES (7, 'ORD202510141905300005', 103, 846.00, 846.00, 0.00, 2, NULL, NULL, '2025-10-14 19:05:31', '2025-10-14 19:05:31');
INSERT INTO `order` VALUES (8, 'ORD202510141909580006', 103, 846.00, 846.00, 0.00, 2, NULL, NULL, '2025-10-14 19:09:59', '2025-10-14 19:09:59');
INSERT INTO `order` VALUES (9, 'ORD202510141913170001', 103, 846.00, 846.00, 0.00, 1, NULL, NULL, '2025-10-14 19:13:17', '2025-10-14 19:13:17');

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
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 2, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-05 17:22:21', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (2, 2, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-05 17:22:21', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (3, 3, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 18:41:32', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (4, 4, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 18:47:14', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (5, 4, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 18:47:14', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (6, 4, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 18:47:14', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (7, 4, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 18:47:14', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (8, 5, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 18:50:02', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (9, 5, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 18:50:02', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (10, 5, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 18:50:02', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (11, 5, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 18:50:02', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (12, 6, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 18:55:51', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (13, 6, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 18:55:51', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (14, 6, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 18:55:51', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (15, 6, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 18:55:51', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (16, 7, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 19:05:31', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (17, 7, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 19:05:31', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (18, 7, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 19:05:31', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (19, 7, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 19:05:31', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (20, 8, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 19:09:59', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (21, 8, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 19:09:59', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (22, 8, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 19:09:59', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (23, 8, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 19:09:59', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (24, 9, 1, 'Java零基础入门教程', 'http://8.141.106.92/media/covers/Java零基础.jpg', 99.00, '2025-10-14 19:13:17', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (25, 9, 5, 'Python机器学习实战', 'http://8.141.106.92/media/covers/Python机器学习实战.jpg', 399.00, '2025-10-14 19:13:17', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (26, 9, 4, 'Python数据分析入门', 'http://8.141.106.92/media/covers/python数据分析入门.png', 149.00, '2025-10-14 19:13:17', '2025-10-19 18:50:02');
INSERT INTO `order_item` VALUES (27, 9, 2, 'Java进阶：Spring Boot实战', 'http://8.141.106.92/media/covers/springboot实战.jpg', 199.00, '2025-10-14 19:13:17', '2025-10-19 18:50:02');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

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
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方交易流水号',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `callback_time` datetime NULL DEFAULT NULL COMMENT '回调时间',
  `callback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回调内容',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_time`(`payment_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 1, 'PAY202501160001', 199.00, 1, 1, NULL, '2025-01-16 10:30:00', NULL, NULL, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `payment_record` VALUES (2, 2, 'PAY202501160002', 299.00, 0, 1, NULL, '2025-01-16 11:15:00', NULL, NULL, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `payment_record` VALUES (14, 9, 'PAY202510141913180001', 846.00, 3, 0, NULL, NULL, NULL, NULL, NULL, '2025-10-14 19:13:18', '2025-10-14 19:13:18');
INSERT INTO `payment_record` VALUES (15, 9, 'PAY202510141916170001', 846.00, 3, 0, NULL, NULL, NULL, NULL, NULL, '2025-10-14 19:16:17', '2025-10-14 19:16:17');
INSERT INTO `payment_record` VALUES (16, 9, 'PAY202510141918360001', 846.00, 3, 1, 'SIM1760440716169', '2025-10-14 19:18:36', '2025-10-14 19:18:36', '{\"status\":\"success\",\"message\":\"模拟支付成功\"}', '模拟支付 - 自动成功', '2025-10-14 19:18:36', '2025-10-14 19:18:36');

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
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, '创建课程', 'course:create', '/api/course/create', '创建新课程', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (2, '编辑课程', 'course:update', '/api/course/update', '编辑课程信息', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (3, '删除课程', 'course:delete', '/api/course/delete', '删除课程', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (4, '发布课程', 'course:publish', '/api/course/*/publish', '发布课程', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (5, '下架课程', 'course:unpublish', '/api/course/*/unpublish', '下架课程', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (6, '浏览课程', 'course:view', '/api/course/**', '浏览课程列表和详情', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (7, '课程审核', 'course:audit', '/api/course/audit', '审核课程内容', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (8, '创建订单', 'order:create', '/api/order/create', '创建购买订单', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (9, '查看订单', 'order:view', '/api/order/**', '查看订单信息', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (10, '查看所有订单', 'order:view:all', '/api/order/all', '查看所有用户订单', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (11, '订单退款', 'order:refund', '/api/order/*/refund', '处理订单退款', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (12, '学习课程', 'learning:study', '/api/learning/**', '学习已购买课程', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (13, '查看学习记录', 'learning:record:view', '/api/learning/record/**', '查看学习记录', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (14, '记录笔记', 'note:create', '/api/note/**', '创建和管理笔记', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (15, '查看所有学习数据', 'learning:view:all', '/api/learning/all', '查看所有用户学习数据', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (16, 'AI智能问答', 'ai:chat', '/api/ai/chat/**', '使用AI智能问答', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (17, 'AI学习分析', 'ai:analysis', '/api/ai/analysis/**', 'AI学习数据分析', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (18, 'AI课程推荐', 'ai:recommend', '/api/ai/recommend/**', 'AI智能课程推荐', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (19, 'AI高级功能', 'ai:advanced', '/api/ai/advanced/**', 'AI高级功能（VIP专享）', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (20, '查看用户列表', 'user:view', '/api/user/list', '查看用户列表', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (21, '创建用户', 'user:create', '/api/user/create', '创建新用户', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (22, '编辑用户', 'user:update', '/api/user/update', '编辑用户信息', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (23, '删除用户', 'user:delete', '/api/user/delete', '删除用户', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (24, '禁用用户', 'user:disable', '/api/user/*/disable', '禁用用户账号', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (25, '启用用户', 'user:enable', '/api/user/*/enable', '启用用户账号', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (26, '查看角色', 'role:view', '/api/role/**', '查看角色信息', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (27, '创建角色', 'role:create', '/api/role/create', '创建新角色', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (28, '编辑角色', 'role:update', '/api/role/update', '编辑角色信息', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (29, '删除角色', 'role:delete', '/api/role/delete', '删除角色', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (30, '分配角色', 'role:assign', '/api/role/assign', '为用户分配角色', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (31, '角色授权', 'role:grant', '/api/role/grant', '为角色分配权限', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (32, '发表评论', 'comment:create', '/api/comment/create', '发表课程评论', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (33, '删除评论', 'comment:delete', '/api/comment/delete', '删除评论', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (34, '审核评论', 'comment:audit', '/api/comment/audit', '审核评论内容', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (35, '系统配置', 'system:config', '/api/system/config', '系统配置管理', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (36, '数据统计', 'system:statistics', '/api/system/stats', '查看数据统计', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `permission` VALUES (37, '日志查看', 'system:log', '/api/system/log', '查看系统日志', '2025-10-02 17:36:59', '2025-10-02 17:36:59');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回复表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reply
-- ----------------------------
INSERT INTO `reply` VALUES (1, 2, 1, '说得很对，我也有同样的感受！', '2025-10-05 15:50:33', '2025-10-05 15:51:51', 2, 1, 1);
INSERT INTO `reply` VALUES (2, 103, 1, '测试111', '2025-10-18 16:35:09', '2025-10-18 16:35:09', NULL, 0, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'SUPER_ADMIN', '超级管理员 - 拥有所有系统权限', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `role` VALUES (2, 'ADMIN', '管理员 - 平台运营管理权限', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `role` VALUES (3, 'TEACHER', '讲师 - 课程创建和管理权限', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `role` VALUES (4, 'VIP_MEMBER', 'VIP会员 - 享受所有课程和高级功能', '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `role` VALUES (5, 'STUDENT', '普通学生 - 基础学习功能', '2025-10-02 17:36:59', '2025-10-02 17:36:59');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 2, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 3, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 4, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 5, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 6, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 7, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 8, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 9, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 10, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 11, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 12, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 13, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 14, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 15, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 16, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 17, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 18, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 19, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 20, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 21, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 22, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 23, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 24, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 25, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 26, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 27, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 28, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 29, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 30, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 31, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 32, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 33, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 34, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 35, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 36, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (1, 37, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 1, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 2, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 3, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 4, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 5, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 6, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 7, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 8, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 9, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 10, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 11, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 12, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 13, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 14, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 15, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 16, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 17, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 18, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 19, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 20, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 21, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 22, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 23, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 24, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 25, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 32, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 33, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 34, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 35, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 36, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (2, 37, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 1, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 2, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 3, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 4, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 5, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 6, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 12, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 13, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 14, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 16, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 17, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 18, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 32, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (3, 33, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 6, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 8, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 9, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 12, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 13, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 14, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 16, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 17, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 18, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 19, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (4, 32, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 6, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 8, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 9, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 12, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 13, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 14, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 16, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 17, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 18, '2025-10-02 17:36:59');
INSERT INTO `role_permission` VALUES (5, 32, '2025-10-02 17:36:59');

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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'Java', '#FF5722', '2025-08-13 21:12:07', '2025-10-05 16:21:55');
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
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别(0:保密,1:男,2:女)',
  `birthday` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生日',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
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
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'testuser', '123456', 'test@example.com', '13888888888', '测试用户', NULL, 0, NULL, NULL, 1, '2025-08-13 19:51:53', '2025-10-14 18:23:59');
INSERT INTO `user` VALUES (100, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'admin@xuetu.com', NULL, '系统管理员', NULL, 0, NULL, NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (101, 'teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'teacher@xuetu.com', NULL, '测试讲师', NULL, 0, NULL, NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (102, 'vip', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'vip@xuetu.com', NULL, 'VIP会员', NULL, 0, NULL, NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (103, 'testuser1', '$2a$10$ZjD45knFafU2YvaLA.y2JuyYv3wK3cKu53kv/NJfRrQur4HNED9y2', 'test@example11.com', '13888888873', '测试用户x', 'http://8.141.106.92/media/UserAvatar/user1.jpg', 1, '2025-10-22', '大家好', 1, '2025-10-03 16:56:03', '2025-10-22 19:15:26');
INSERT INTO `user` VALUES (104, 'test555', '$2a$10$TvLdN49GnedsJyto8NAukOGggtycLlHXNpx1gi7X6k.m/43Wgwhhe', '2052619274@qq.com', '19391629120', 'test555', NULL, 0, NULL, NULL, 1, '2025-10-13 19:47:26', '2025-10-13 19:47:26');

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户课程关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_course
-- ----------------------------
INSERT INTO `user_course` VALUES (1, 1, 1, 1, 199.00, '2025-01-16 10:31:00', 50, '2025-10-05 16:41:05', 1, NULL, '2025-08-16 19:14:23', '2025-10-05 16:41:05');
INSERT INTO `user_course` VALUES (2, 1, 2, 2, 299.00, '2025-01-16 11:16:00', 33, '2025-09-09 18:23:44', 1, NULL, '2025-08-16 19:14:23', '2025-09-09 18:23:44');
INSERT INTO `user_course` VALUES (3, 2, 1, NULL, 199.00, '2025-01-15 09:20:00', 80, NULL, 1, NULL, '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `user_course` VALUES (4, 103, 1, 9, 99.00, '2025-10-14 19:18:36', 16, '2025-10-22 17:45:45', 1, NULL, '2025-10-14 19:18:36', '2025-10-22 17:45:45');
INSERT INTO `user_course` VALUES (5, 103, 5, 9, 399.00, '2025-10-14 19:18:36', 0, '2025-10-18 19:18:25', 1, NULL, '2025-10-14 19:18:36', '2025-10-18 19:18:25');
INSERT INTO `user_course` VALUES (6, 103, 4, 9, 149.00, '2025-10-14 19:18:36', 8, '2025-10-26 18:43:06', 1, NULL, '2025-10-14 19:18:36', '2025-10-26 18:43:06');
INSERT INTO `user_course` VALUES (7, 103, 2, 9, 199.00, '2025-10-14 19:18:36', 16, '2025-10-19 18:43:36', 1, NULL, '2025-10-14 19:18:36', '2025-10-19 18:43:36');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 5, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (100, 1, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (101, 3, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (102, 4, '2025-10-02 17:36:59');

SET FOREIGN_KEY_CHECKS = 1;
