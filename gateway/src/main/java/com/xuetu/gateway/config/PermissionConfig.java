package com.xuetu.gateway.config;

import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * 权限配置 - URL与角色映射关系
 * 
 * @author star
 */
@Configuration
public class PermissionConfig {

    /**
     * URL权限映射表
     * key: URL路径（支持通配符**）
     * value: 需要的角色列表
     */
    private static final Map<String, List<String>> URL_PERMISSIONS = new LinkedHashMap<>();

    static {
        // ==================== 课程管理权限 ====================
        // 创建课程 - 需要讲师或管理员
        URL_PERMISSIONS.put("/api/course/create", Arrays.asList("TEACHER", "ADMIN", "SUPER_ADMIN"));
        URL_PERMISSIONS.put("/api/course/update", Arrays.asList("TEACHER", "ADMIN", "SUPER_ADMIN"));
        URL_PERMISSIONS.put("/api/course/delete", Arrays.asList("TEACHER", "ADMIN", "SUPER_ADMIN"));
        
        // 发布/下架课程 - 需要讲师或管理员
        URL_PERMISSIONS.put("/api/course/.*/publish", Arrays.asList("TEACHER", "ADMIN", "SUPER_ADMIN"));
        URL_PERMISSIONS.put("/api/course/.*/unpublish", Arrays.asList("TEACHER", "ADMIN", "SUPER_ADMIN"));

        // ==================== 订单管理权限 ====================
        // 查看所有订单 - 只有管理员
        URL_PERMISSIONS.put("/api/order/all", Arrays.asList("ADMIN", "SUPER_ADMIN"));
        
        // 订单退款 - 只有管理员
        URL_PERMISSIONS.put("/api/order/.*/refund", Arrays.asList("ADMIN", "SUPER_ADMIN"));

        // ==================== 角色权限管理 ====================
        // 角色管理 - 只有超级管理员和管理员
        URL_PERMISSIONS.put("/api/role/create", Arrays.asList("SUPER_ADMIN", "ADMIN"));
        URL_PERMISSIONS.put("/api/role/update", Arrays.asList("SUPER_ADMIN", "ADMIN"));
        URL_PERMISSIONS.put("/api/role/delete", Arrays.asList("SUPER_ADMIN", "ADMIN"));
        URL_PERMISSIONS.put("/api/role/assign", Arrays.asList("SUPER_ADMIN", "ADMIN"));
        URL_PERMISSIONS.put("/api/role/grant", Arrays.asList("SUPER_ADMIN", "ADMIN"));

        // 权限管理 - 只有超级管理员
        URL_PERMISSIONS.put("/api/permission/create", Collections.singletonList("SUPER_ADMIN"));
        URL_PERMISSIONS.put("/api/permission/update", Collections.singletonList("SUPER_ADMIN"));
        URL_PERMISSIONS.put("/api/permission/delete", Collections.singletonList("SUPER_ADMIN"));

        // ==================== 评论管理权限 ====================
        // 删除评论 - 管理员或讲师
        URL_PERMISSIONS.put("/api/comment/delete", Arrays.asList("ADMIN", "SUPER_ADMIN", "TEACHER"));
        
        // 审核评论 - 管理员
        URL_PERMISSIONS.put("/api/comment/audit", Arrays.asList("ADMIN", "SUPER_ADMIN"));

        // ==================== 学习记录管理 ====================
        // 查看所有学习记录 - 只有管理员
        URL_PERMISSIONS.put("/api/learning/all", Arrays.asList("ADMIN", "SUPER_ADMIN"));
    }

    /**
     * 白名单 - 不需要权限验证的URL
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/api/user/auth/login",
        "/api/user/auth/register",
        "/api/user/auth/check-username",
        "/api/user/auth/check-email",
        "/api/user/auth/health",
        "/api/course/list",           // 课程列表公开
        "/api/course/\\d+",            // 课程详情公开
        "/api/course/search",          // 课程搜索公开
        "/api/category/.*",            // 分类公开
        "/api/tag/.*",                 // 标签公开
        "/doc.html",                   // API文档
        "/v3/api-docs.*",              // API文档
        "/webjars/.*",                 // 静态资源
        "/favicon.ico"                 // 图标
    );

    /**
     * 获取URL需要的角色列表
     * 
     * @param path 请求路径
     * @return 角色列表，null表示不需要特殊权限
     */
    public static List<String> getRequiredRoles(String path) {
        // 检查白名单
        for (String pattern : WHITE_LIST) {
            if (path.matches(pattern)) {
                return null;  // 白名单，不需要权限
            }
        }

        // 检查权限映射
        for (Map.Entry<String, List<String>> entry : URL_PERMISSIONS.entrySet()) {
            String pattern = entry.getKey();
            // 将路径模式转换为正则表达式
            String regex = pattern
                    .replace("**", ".*")
                    .replace("*", "[^/]*")
                    .replace(".", "\\.");
            
            if (path.matches(regex)) {
                return entry.getValue();
            }
        }

        // 不在配置中的URL，默认需要登录但不需要特殊角色
        return Collections.emptyList();
    }

    /**
     * 检查用户是否有任意一个所需角色
     * 
     * @param userRoles 用户角色列表
     * @param requiredRoles 需要的角色列表
     * @return 是否有权限
     */
    public static boolean hasAnyRole(List<String> userRoles, List<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;  // 不需要特殊角色
        }
        
        if (userRoles == null || userRoles.isEmpty()) {
            return false;  // 用户没有角色
        }

        // 检查是否有交集
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }

    /**
     * 判断URL是否在白名单中
     */
    public static boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(path::matches);
    }
}
