-- ========================================
-- 学途教育平台 - 角色权限初始化数据
-- ========================================
-- 说明：
-- 1. 初始化基础角色（管理员、讲师、学生、VIP）
-- 2. 初始化核心权限
-- 3. 配置角色权限关系
-- 4. 创建默认管理员账号
-- ========================================

SET NAMES utf8mb4;

-- ========================================
-- 1. 初始化角色数据
-- ========================================
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES
(1, 'SUPER_ADMIN', '超级管理员 - 拥有所有系统权限'),
(2, 'ADMIN', '管理员 - 平台运营管理权限'),
(3, 'TEACHER', '讲师 - 课程创建和管理权限'),
(4, 'VIP_MEMBER', 'VIP会员 - 享受所有课程和高级功能'),
(5, 'STUDENT', '普通学生 - 基础学习功能');

-- ========================================
-- 2. 初始化权限数据
-- ========================================

-- 2.1 课程管理权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(1, '创建课程', 'course:create', '/api/course/create', '创建新课程'),
(2, '编辑课程', 'course:update', '/api/course/update', '编辑课程信息'),
(3, '删除课程', 'course:delete', '/api/course/delete', '删除课程'),
(4, '发布课程', 'course:publish', '/api/course/*/publish', '发布课程'),
(5, '下架课程', 'course:unpublish', '/api/course/*/unpublish', '下架课程'),
(6, '浏览课程', 'course:view', '/api/course/**', '浏览课程列表和详情'),
(7, '课程审核', 'course:audit', '/api/course/audit', '审核课程内容');

-- 2.2 订单管理权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(8, '创建订单', 'order:create', '/api/order/create', '创建购买订单'),
(9, '查看订单', 'order:view', '/api/order/**', '查看订单信息'),
(10, '查看所有订单', 'order:view:all', '/api/order/all', '查看所有用户订单'),
(11, '订单退款', 'order:refund', '/api/order/*/refund', '处理订单退款');

-- 2.3 学习功能权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(12, '学习课程', 'learning:study', '/api/learning/**', '学习已购买课程'),
(13, '查看学习记录', 'learning:record:view', '/api/learning/record/**', '查看学习记录'),
(14, '记录笔记', 'note:create', '/api/note/**', '创建和管理笔记'),
(15, '查看所有学习数据', 'learning:view:all', '/api/learning/all', '查看所有用户学习数据');

-- 2.4 AI功能权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(16, 'AI智能问答', 'ai:chat', '/api/ai/chat/**', '使用AI智能问答'),
(17, 'AI学习分析', 'ai:analysis', '/api/ai/analysis/**', 'AI学习数据分析'),
(18, 'AI课程推荐', 'ai:recommend', '/api/ai/recommend/**', 'AI智能课程推荐'),
(19, 'AI高级功能', 'ai:advanced', '/api/ai/advanced/**', 'AI高级功能（VIP专享）');

-- 2.5 用户管理权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(20, '查看用户列表', 'user:view', '/api/user/list', '查看用户列表'),
(21, '创建用户', 'user:create', '/api/user/create', '创建新用户'),
(22, '编辑用户', 'user:update', '/api/user/update', '编辑用户信息'),
(23, '删除用户', 'user:delete', '/api/user/delete', '删除用户'),
(24, '禁用用户', 'user:disable', '/api/user/*/disable', '禁用用户账号'),
(25, '启用用户', 'user:enable', '/api/user/*/enable', '启用用户账号');

-- 2.6 角色权限管理
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(26, '查看角色', 'role:view', '/api/role/**', '查看角色信息'),
(27, '创建角色', 'role:create', '/api/role/create', '创建新角色'),
(28, '编辑角色', 'role:update', '/api/role/update', '编辑角色信息'),
(29, '删除角色', 'role:delete', '/api/role/delete', '删除角色'),
(30, '分配角色', 'role:assign', '/api/role/assign', '为用户分配角色'),
(31, '角色授权', 'role:grant', '/api/role/grant', '为角色分配权限');

-- 2.7 评论管理权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(32, '发表评论', 'comment:create', '/api/comment/create', '发表课程评论'),
(33, '删除评论', 'comment:delete', '/api/comment/delete', '删除评论'),
(34, '审核评论', 'comment:audit', '/api/comment/audit', '审核评论内容');

-- 2.8 系统管理权限
INSERT INTO `permission` (`id`, `permission_name`, `permission_key`, `url`, `description`) VALUES
(35, '系统配置', 'system:config', '/api/system/config', '系统配置管理'),
(36, '数据统计', 'system:statistics', '/api/system/stats', '查看数据统计'),
(37, '日志查看', 'system:log', '/api/system/log', '查看系统日志');

-- ========================================
-- 3. 角色权限关联配置
-- ========================================

-- 3.1 超级管理员 (SUPER_ADMIN) - 拥有所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM permission;

-- 3.2 管理员 (ADMIN) - 运营管理权限
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
-- 课程管理
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
-- 订单管理（全部）
(2, 8), (2, 9), (2, 10), (2, 11),
-- 学习数据（全部）
(2, 12), (2, 13), (2, 14), (2, 15),
-- AI功能（全部）
(2, 16), (2, 17), (2, 18), (2, 19),
-- 用户管理（全部）
(2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25),
-- 评论管理
(2, 32), (2, 33), (2, 34),
-- 系统管理
(2, 35), (2, 36), (2, 37);

-- 3.3 讲师 (TEACHER) - 课程教学权限
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
-- 课程管理（自己的）
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6),
-- 学习功能
(3, 12), (3, 13), (3, 14),
-- AI功能
(3, 16), (3, 17), (3, 18),
-- 评论管理（自己课程的）
(3, 32), (3, 33);

-- 3.4 VIP会员 (VIP_MEMBER) - 高级学习权限
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
-- 浏览课程（免费）
(4, 6),
-- 订单（可以看自己的）
(4, 8), (4, 9),
-- 学习功能（全部）
(4, 12), (4, 13), (4, 14),
-- AI功能（包含高级）
(4, 16), (4, 17), (4, 18), (4, 19),
-- 评论
(4, 32);

-- 3.5 普通学生 (STUDENT) - 基础学习权限
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
-- 浏览课程
(5, 6),
-- 订单（只能操作自己的）
(5, 8), (5, 9),
-- 学习功能
(5, 12), (5, 13), (5, 14),
-- AI基础功能（不含高级）
(5, 16), (5, 17), (5, 18),
-- 评论
(5, 32);

-- ========================================
-- 4. 创建默认管理员账号
-- ========================================

-- 4.1 创建默认超级管理员（如果不存在）
-- 用户名: admin, 密码: admin123 (实际应该加密)
INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `status`) 
VALUES (100, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'admin@xuetu.com', '系统管理员', 1)
ON DUPLICATE KEY UPDATE username = username;

-- 4.2 为管理员分配超级管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`) 
VALUES (100, 1)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 4.3 创建测试讲师账号
-- 用户名: teacher, 密码: teacher123
INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `status`) 
VALUES (101, 'teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'teacher@xuetu.com', '测试讲师', 1)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO `user_role` (`user_id`, `role_id`) 
VALUES (101, 3)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 4.4 创建测试VIP会员账号
-- 用户名: vip, 密码: vip123
INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `status`) 
VALUES (102, 'vip', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lLy6fFz2JVHfHDcXm', 'vip@xuetu.com', 'VIP会员', 1)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO `user_role` (`user_id`, `role_id`) 
VALUES (102, 4)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 4.5 为已存在的普通用户分配学生角色（用户ID=1）
INSERT INTO `user_role` (`user_id`, `role_id`) 
VALUES (1, 5)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- ========================================
-- 完成初始化
-- ========================================

-- 查询统计信息
SELECT '=== 角色权限初始化完成 ===' AS info;
SELECT CONCAT('共创建 ', COUNT(*), ' 个角色') AS role_count FROM role;
SELECT CONCAT('共创建 ', COUNT(*), ' 个权限') AS permission_count FROM permission;
SELECT CONCAT('共配置 ', COUNT(*), ' 条角色权限关系') AS role_permission_count FROM role_permission;
SELECT CONCAT('共创建 ', COUNT(*), ' 个测试用户') AS user_count FROM user WHERE id >= 100;

-- 显示角色权限分配情况
SELECT 
    r.role_name AS '角色',
    r.description AS '描述',
    COUNT(rp.permission_id) AS '权限数量'
FROM role r
LEFT JOIN role_permission rp ON r.id = rp.role_id
GROUP BY r.id, r.role_name, r.description
ORDER BY r.id;

SELECT '=== 测试账号信息 ===' AS info;
SELECT 
    u.username AS '用户名',
    u.email AS '邮箱',
    r.role_name AS '角色',
    '密码统一为: username123' AS '密码说明'
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
WHERE u.id >= 100
ORDER BY u.id;
