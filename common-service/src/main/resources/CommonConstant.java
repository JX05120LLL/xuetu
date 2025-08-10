package com.star.common.constant;

/**
 * 公共常量
 * @author star
 */
public interface CommonConstant {

    /**
     * 成功状态码
     */
    Integer SUCCESS = 200;

    /**
     * 失败状态码
     */
    Integer FAIL = 500;

    /**
     * 用户状态 - 启用
     */
    Integer USER_STATUS_ENABLE = 1;

    /**
     * 用户状态 - 禁用
     */
    Integer USER_STATUS_DISABLE = 0;

    /**
     * 订单状态 - 待支付
     */
    Integer ORDER_STATUS_UNPAID = 0;

    /**
     * 订单状态 - 已支付
     */
    Integer ORDER_STATUS_PAID = 1;

    /**
     * 订单状态 - 已取消
     */
    Integer ORDER_STATUS_CANCELLED = 2;

    /**
     * 课程状态 - 草稿
     */
    Integer COURSE_STATUS_DRAFT = 0;

    /**
     * 课程状态 - 发布
     */
    Integer COURSE_STATUS_PUBLISHED = 1;

    /**
     * 课程状态 - 下架
     */
    Integer COURSE_STATUS_OFFLINE = 2;

    /**
     * 默认分页大小
     */
    Integer DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大分页大小
     */
    Integer MAX_PAGE_SIZE = 100;

    /**
     * JWT Token请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT Token前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 缓存键前缀 - 用户信息
     */
    String CACHE_USER_PREFIX = "user:";

    /**
     * 缓存键前缀 - 课程信息
     */
    String CACHE_COURSE_PREFIX = "course:";

    /**
     * 缓存键前缀 - 热门课程
     */
    String CACHE_HOT_COURSE = "hot:course";

    /**
     * 缓存键前缀 - 验证码
     */
    String CACHE_CAPTCHA_PREFIX = "captcha:";

    /**
     * 分布式锁前缀 - 订单
     */
    String LOCK_ORDER_PREFIX = "lock:order:";

    /**
     * 分布式锁前缀 - 课程
     */
    String LOCK_COURSE_PREFIX = "lock:course:";

    /**
     * 默认角色 - 学生
     */
    String DEFAULT_ROLE_STUDENT = "STUDENT";

    /**
     * 角色 - 教师
     */
    String ROLE_TEACHER = "TEACHER";

    /**
     * 角色 - 管理员
     */
    String ROLE_ADMIN = "ADMIN";
}