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

 Date: 14/10/2025 19:04:31
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
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '章节表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话历史表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 1, '这门课程很棒，讲解得很详细！', 5, '2025-08-13 22:40:53', '2025-10-05 15:49:10', 0, 1, 0, 0);
INSERT INTO `comment` VALUES (2, 1, 1, '这门课程很棒，讲解得很详细！测试评论', 5, '2025-10-05 15:47:25', '2025-10-05 15:53:49', 0, 0, 0, 0);

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
INSERT INTO `course` VALUES (1, 'Java零基础入门教程', '从零开始学习Java编程语言，包含语法基础、面向对象编程、异常处理等核心知识点。适合编程初学者，通过大量实战项目快速掌握Java开发技能。', 'https://www.itcast.cn/files/image/202103/20210302154551447.png', 99.00, 199.00, 1, 100, 0, 1, 1200, 1570, '2025-08-13 21:07:18', '2025-10-05 18:45:49');
INSERT INTO `course` VALUES (2, 'Java进阶：Spring Boot实战', '深入学习Spring Boot框架，从入门到精通。包含自动配置、起步依赖、Actuator监控、部署等高级特性。通过实际项目开发掌握企业级开发技能。', 'https://example.com/covers/springboot.jpg', 199.00, 299.00, 1, 102, 1, 1, 1800, 856, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (3, 'Java微服务架构实战', '学习微服务架构设计与实现，包含Spring Cloud、Docker、Kubernetes等技术栈。通过大型项目实战，掌握分布式系统开发与部署。', 'https://example.com/covers/microservice.jpg', 299.00, 399.00, 1, 101, 2, 1, 2400, 423, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (4, 'Python数据分析入门', '学习Python数据分析的核心库：NumPy、Pandas、Matplotlib。从数据清洗到可视化，掌握数据分析的完整流程。', 'https://example.com/covers/python-data.jpg', 149.00, 249.00, 2, 11, 0, 1, 1500, 732, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (5, 'Python机器学习实战', '深入学习机器学习算法与实现，包含监督学习、无监督学习、深度学习等。使用scikit-learn、TensorFlow等主流框架。', 'https://example.com/covers/python-ml.jpg', 399.00, 599.00, 2, 5, 2, 1, 3000, 289, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (6, 'Vue.js 3.0 全家桶开发', '学习Vue.js 3.0的新特性，包含Composition API、Vite构建工具、Vue Router、Vuex状态管理等。通过实战项目掌握现代前端开发。', 'https://example.com/covers/vue3.jpg', 179.00, 279.00, 3, 20, 1, 1, 1600, 945, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (7, 'React企业级开发实战', '深入学习React生态系统，包含Hooks、Redux、React Router、Next.js等。通过大型SPA项目掌握企业级前端开发技能。', 'https://example.com/covers/react.jpg', 229.00, 329.00, 3, 21, 1, 1, 2000, 567, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (8, 'MySQL数据库设计与优化', '系统学习MySQL数据库，从基础语法到高级优化。包含索引设计、查询优化、存储过程、主从复制等企业级技能。', 'https://example.com/covers/mysql.jpg', 129.00, 199.00, 4, 30, 0, 1, 1000, 1234, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (9, 'Redis缓存技术实战', '深入学习Redis缓存技术，包含数据类型、持久化、集群、哨兵等高级特性。掌握高并发系统的缓存设计与优化。', 'https://example.com/covers/redis.jpg', 199.00, 299.00, 4, 31, 1, 1, 1200, 678, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `course` VALUES (10, 'Kubernetes容器编排', '学习Kubernetes容器编排技术，包含Pod、Service、Deployment等核心概念。掌握云原生应用的部署与管理。', 'https://example.com/covers/k8s.jpg', 349.00, 449.00, 5, 1, 2, 2, 2200, 0, '2025-08-13 21:07:18', '2025-10-05 16:06:12');
INSERT INTO `course` VALUES (11, 'Java基础入门教程，测试数据', '从零开始学习Java编程语言', 'https://example.com/cover.jpg', 99.00, 199.00, 1, 1, 0, 0, 0, 0, '2025-10-05 16:01:05', '2025-10-05 16:01:05');

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
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课时表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lesson
-- ----------------------------
INSERT INTO `lesson` VALUES (1, 1, '1.1 测试数据', 'https://example.com/video.mp4', 30, 1, 1, '2025-08-13 21:07:18', '2025-10-05 16:17:34');
INSERT INTO `lesson` VALUES (2, 1, '1.2 JDK安装与配置', 'https://example.com/videos/java-1-2.mp4', 25, 1, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (3, 1, '1.3 开发工具IDEA安装', 'https://example.com/videos/java-1-3.mp4', 20, 1, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (4, 1, '1.4 第一个Java程序', 'https://example.com/videos/java-1-4.mp4', 30, 0, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (11, 2, '2.2 变量与常量', 'https://example.com/videos/java-2-2.mp4', 25, 0, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (12, 2, '2.3 运算符与表达式', 'https://example.com/videos/java-2-3.mp4', 40, 0, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (13, 2, '2.4 控制流程语句', 'https://example.com/videos/java-2-4.mp4', 45, 0, 4, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (20, 10, '1.1 Spring Boot简介', 'https://example.com/videos/sb-1-1.mp4', 20, 1, 1, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (21, 10, '1.2 创建第一个Spring Boot项目', 'https://example.com/videos/sb-1-2.mp4', 35, 1, 2, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (22, 10, '1.3 项目结构分析', 'https://example.com/videos/sb-1-3.mp4', 25, 0, 3, '2025-08-13 21:07:18', '2025-08-13 21:07:18');
INSERT INTO `lesson` VALUES (23, 1, '1.1 Java环境搭建test', 'https://example.com/video.mp4', 30, 1, 1, '2025-10-05 16:13:59', '2025-10-05 16:13:59');

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (2, 'ORD202510051722210003', 1, 548.00, 548.00, 0.00, 0, '购买Java课程', NULL, '2025-10-05 17:22:21', '2025-10-05 17:22:21');
INSERT INTO `order` VALUES (3, 'ORD202510141841310001', 103, 99.00, 99.00, 0.00, 0, NULL, NULL, '2025-10-14 18:41:32', '2025-10-14 18:41:32');
INSERT INTO `order` VALUES (4, 'ORD202510141847130002', 103, 846.00, 846.00, 0.00, 0, NULL, NULL, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order` VALUES (5, 'ORD202510141850010003', 103, 846.00, 846.00, 0.00, 0, NULL, NULL, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order` VALUES (6, 'ORD202510141855500004', 103, 846.00, 846.00, 0.00, 0, NULL, NULL, '2025-10-14 18:55:51', '2025-10-14 18:55:51');

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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 2, 4, 'Python数据分析入门', 'https://example.com/covers/python-data.jpg', 149.00, '2025-10-05 17:22:21', '2025-10-05 17:22:21');
INSERT INTO `order_item` VALUES (2, 2, 5, 'Python机器学习实战', 'https://example.com/covers/python-ml.jpg', 399.00, '2025-10-05 17:22:21', '2025-10-05 17:22:21');
INSERT INTO `order_item` VALUES (3, 3, 1, 'Java零基础入门教程', 'https://www.itcast.cn/files/image/202103/20210302154551447.png', 99.00, '2025-10-14 18:41:32', '2025-10-14 18:41:32');
INSERT INTO `order_item` VALUES (4, 4, 1, 'Java零基础入门教程', 'https://www.itcast.cn/files/image/202103/20210302154551447.png', 99.00, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order_item` VALUES (5, 4, 5, 'Python机器学习实战', 'https://example.com/covers/python-ml.jpg', 399.00, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order_item` VALUES (6, 4, 4, 'Python数据分析入门', 'https://example.com/covers/python-data.jpg', 149.00, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order_item` VALUES (7, 4, 2, 'Java进阶：Spring Boot实战', 'https://example.com/covers/springboot.jpg', 199.00, '2025-10-14 18:47:14', '2025-10-14 18:47:14');
INSERT INTO `order_item` VALUES (8, 5, 1, 'Java零基础入门教程', 'https://www.itcast.cn/files/image/202103/20210302154551447.png', 99.00, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order_item` VALUES (9, 5, 5, 'Python机器学习实战', 'https://example.com/covers/python-ml.jpg', 399.00, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order_item` VALUES (10, 5, 4, 'Python数据分析入门', 'https://example.com/covers/python-data.jpg', 149.00, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order_item` VALUES (11, 5, 2, 'Java进阶：Spring Boot实战', 'https://example.com/covers/springboot.jpg', 199.00, '2025-10-14 18:50:02', '2025-10-14 18:50:02');
INSERT INTO `order_item` VALUES (12, 6, 1, 'Java零基础入门教程', 'https://www.itcast.cn/files/image/202103/20210302154551447.png', 99.00, '2025-10-14 18:55:51', '2025-10-14 18:55:51');
INSERT INTO `order_item` VALUES (13, 6, 5, 'Python机器学习实战', 'https://example.com/covers/python-ml.jpg', 399.00, '2025-10-14 18:55:51', '2025-10-14 18:55:51');
INSERT INTO `order_item` VALUES (14, 6, 4, 'Python数据分析入门', 'https://example.com/covers/python-data.jpg', 149.00, '2025-10-14 18:55:51', '2025-10-14 18:55:51');
INSERT INTO `order_item` VALUES (15, 6, 2, 'Java进阶：Spring Boot实战', 'https://example.com/covers/springboot.jpg', 199.00, '2025-10-14 18:55:51', '2025-10-14 18:55:51');

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
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_time`(`payment_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 1, 'PAY202501160001', 199.00, 1, 1, '2025-01-16 10:30:00', '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `payment_record` VALUES (2, 2, 'PAY202501160002', 299.00, 0, 1, '2025-01-16 11:15:00', '2025-08-16 19:14:23', '2025-08-16 19:14:23');
INSERT INTO `payment_record` VALUES (3, 5, 'PAY202510141855040001', 846.00, 3, 0, NULL, '2025-10-14 18:55:04', '2025-10-14 18:55:04');
INSERT INTO `payment_record` VALUES (4, 6, 'PAY202510141855530002', 846.00, 3, 0, NULL, '2025-10-14 18:55:54', '2025-10-14 18:55:54');
INSERT INTO `payment_record` VALUES (5, 6, 'PAY202510141856100003', 846.00, 2, 0, NULL, '2025-10-14 18:56:10', '2025-10-14 18:56:10');
INSERT INTO `payment_record` VALUES (6, 6, 'PAY202510141856120004', 846.00, 1, 0, NULL, '2025-10-14 18:56:13', '2025-10-14 18:56:13');
INSERT INTO `payment_record` VALUES (7, 6, 'PAY202510141856170005', 846.00, 3, 0, NULL, '2025-10-14 18:56:17', '2025-10-14 18:56:17');
INSERT INTO `payment_record` VALUES (8, 6, 'PAY202510141856210006', 846.00, 3, 0, NULL, '2025-10-14 18:56:22', '2025-10-14 18:56:22');
INSERT INTO `payment_record` VALUES (9, 6, 'PAY202510141856510007', 846.00, 3, 0, NULL, '2025-10-14 18:56:52', '2025-10-14 18:56:52');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回复表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reply
-- ----------------------------
INSERT INTO `reply` VALUES (1, 2, 1, '说得很对，我也有同样的感受！', '2025-10-05 15:50:33', '2025-10-05 15:51:51', 2, 1, 1);

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
INSERT INTO `user` VALUES (1, 'testuser', '123456', 'test@example.com', '13888888888', '测试用户', NULL, 1, '2025-08-13 19:51:53', '2025-10-14 18:23:59');
INSERT INTO `user` VALUES (100, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'admin@xuetu.com', NULL, '系统管理员', NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (101, 'teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'teacher@xuetu.com', NULL, '测试讲师', NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (102, 'vip', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'vip@xuetu.com', NULL, 'VIP会员', NULL, 1, '2025-10-02 17:36:59', '2025-10-02 17:36:59');
INSERT INTO `user` VALUES (103, 'testuser1', '$2a$10$WoEWi1JM3JxfVmS8UaiupOXaFEnUByq1O2SVezxaWL5e816h.iDqC', 'test@example11.com', '13888888878', '测试用户1', NULL, 1, '2025-10-03 16:56:03', '2025-10-03 16:56:03');
INSERT INTO `user` VALUES (104, 'test555', '$2a$10$TvLdN49GnedsJyto8NAukOGggtycLlHXNpx1gi7X6k.m/43Wgwhhe', '2052619274@qq.com', '19391629120', 'test555', NULL, 1, '2025-10-13 19:47:26', '2025-10-13 19:47:26');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户课程关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_course
-- ----------------------------
INSERT INTO `user_course` VALUES (1, 1, 1, 1, 199.00, '2025-01-16 10:31:00', 50, '2025-10-05 16:41:05', 1, NULL, '2025-08-16 19:14:23', '2025-10-05 16:41:05');
INSERT INTO `user_course` VALUES (2, 1, 2, 2, 299.00, '2025-01-16 11:16:00', 33, '2025-09-09 18:23:44', 1, NULL, '2025-08-16 19:14:23', '2025-09-09 18:23:44');
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 5, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (100, 1, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (101, 3, '2025-10-02 17:36:59');
INSERT INTO `user_role` VALUES (102, 4, '2025-10-02 17:36:59');

SET FOREIGN_KEY_CHECKS = 1;
