-- 合并版数据库 - xuetu_db

-- 创建数据库
CREATE DATABASE IF NOT EXISTS xuetu_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xuetu_db;

-- ====================== 用户服务相关表 ======================

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户信息表';

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名称',
    permission_key VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识',
    url VARCHAR(255) COMMENT '请求URL',
    description VARCHAR(255) COMMENT '权限描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) COMMENT '用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) COMMENT '角色权限关联表';

-- ====================== 课程服务相关表 ======================

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    title VARCHAR(100) NOT NULL COMMENT '课程标题',
    description TEXT COMMENT '课程描述',
    cover_image VARCHAR(255) COMMENT '封面图片URL',
    price DECIMAL(10, 2) DEFAULT 0.00 COMMENT '课程价格',
    original_price DECIMAL(10, 2) DEFAULT 0.00 COMMENT '原价',
    teacher_id BIGINT COMMENT '讲师ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    level TINYINT DEFAULT 0 COMMENT '难度级别(0:初级,1:中级,2:高级)',
    status TINYINT DEFAULT 1 COMMENT '状态(0:未发布,1:已发布,2:已下架)',
    total_duration INT DEFAULT 0 COMMENT '总时长(分钟)',
    student_count INT DEFAULT 0 COMMENT '学习人数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '课程表';

-- 章节表
CREATE TABLE IF NOT EXISTS chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '章节ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(100) NOT NULL COMMENT '章节标题',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) COMMENT '章节表';

-- 课时表
CREATE TABLE IF NOT EXISTS lesson (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课时ID',
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    title VARCHAR(100) NOT NULL COMMENT '课时标题',
    video_url VARCHAR(255) COMMENT '视频URL',
    duration INT DEFAULT 0 COMMENT '时长(分钟)',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费(0:否,1:是)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE
) COMMENT '课时表';

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '标签表';

-- 课程标签关联表
CREATE TABLE IF NOT EXISTS course_tag (
    course_id BIGINT NOT NULL COMMENT '课程ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) COMMENT '课程标签关联表';

-- ====================== 订单服务相关表 ======================

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    status TINYINT DEFAULT 0 COMMENT '订单状态(0:待支付,1:已支付,2:已取消,3:已退款,4:已完成)',
    payment_time DATETIME COMMENT '支付时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_title VARCHAR(100) NOT NULL COMMENT '课程标题',
    course_cover VARCHAR(255) COMMENT '课程封面',
    price DECIMAL(10, 2) NOT NULL COMMENT '购买价格',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE
) COMMENT '订单项表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_no VARCHAR(50) COMMENT '支付流水号',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    payment_type TINYINT DEFAULT 0 COMMENT '支付方式(0:微信,1:支付宝,2:银行卡)',
    status TINYINT DEFAULT 0 COMMENT '支付状态(0:待支付,1:支付成功,2:支付失败)',
    payment_time DATETIME COMMENT '支付时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_id (order_id)
) COMMENT '支付记录表';

-- ====================== 学习服务相关表 ======================

-- 学习记录表
CREATE TABLE IF NOT EXISTS learning_record (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    lesson_id BIGINT NOT NULL COMMENT '课时ID',
    progress INT DEFAULT 0 COMMENT '学习进度(秒)',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成(0:否,1:是)',
    last_learn_time DATETIME COMMENT '最后学习时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id, lesson_id)
) COMMENT '学习记录表';

-- 笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    lesson_id BIGINT NOT NULL COMMENT '课时ID',
    title VARCHAR(100) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '笔记表';

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    content TEXT NOT NULL COMMENT '评论内容',
    rating TINYINT COMMENT '评分(1-5)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '评论表';

-- 回复表
CREATE TABLE IF NOT EXISTS reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回复ID',
    user_id BIGINT NOT NULL COMMENT '回复用户ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    content TEXT NOT NULL COMMENT '回复内容',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE
) COMMENT '回复表';