package com.star.common.constant;

/**
 * 公共常量
 * @author star
 */
public class CommonConstant {

    /**
     * 默认页码
     */
    public static final Integer DEFAULT_CURRENT = 1;

    /**
     * 默认分页大小
     */
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大分页大小
     */
    public static final Integer MAX_PAGE_SIZE = 500;

    /**
     * JWT相关常量
     */
    public static final class JWT {
        /**
         * JWT密钥
         */
        public static final String SECRET = "xuetu_education_platform_secret_key";
        
        /**
         * JWT过期时间（7天）
         */
        public static final Long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
        
        /**
         * JWT令牌前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";
        
        /**
         * JWT请求头名称
         */
        public static final String TOKEN_HEADER = "Authorization";
        
        /**
         * 用户ID键名
         */
        public static final String USER_ID_KEY = "userId";
        
        /**
         * 用户名键名
         */
        public static final String USERNAME_KEY = "username";
    }

    /**
     * Redis相关常量
     */
    public static final class REDIS {
        /**
         * 用户信息缓存前缀
         */
        public static final String USER_INFO_PREFIX = "user:info:";
        
        /**
         * 用户权限缓存前缀
         */
        public static final String USER_PERMISSION_PREFIX = "user:permission:";
        
        /**
         * JWT黑名单前缀
         */
        public static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";
        
        /**
         * 课程信息缓存前缀
         */
        public static final String COURSE_INFO_PREFIX = "course:info:";
        
        /**
         * 热门课程缓存键
         */
        public static final String HOT_COURSES_KEY = "course:hot";
        
        /**
         * 分布式锁前缀
         */
        public static final String LOCK_PREFIX = "lock:";
        
        /**
         * 验证码前缀
         */
        public static final String CAPTCHA_PREFIX = "captcha:";
        
        /**
         * 限流前缀
         */
        public static final String RATE_LIMIT_PREFIX = "rate:limit:";
        
        /**
         * 默认缓存过期时间（1小时）
         */
        public static final Long DEFAULT_EXPIRE_TIME = 60 * 60L;
        
        /**
         * 用户信息缓存过期时间（30分钟）
         */
        public static final Long USER_INFO_EXPIRE_TIME = 30 * 60L;
        
        /**
         * 验证码过期时间（5分钟）
         */
        public static final Long CAPTCHA_EXPIRE_TIME = 5 * 60L;
    }

    /**
     * 订单相关常量
     */
    public static final class ORDER {
        /**
         * 订单状态 - 待支付
         */
        public static final String STATUS_PENDING = "PENDING";
        
        /**
         * 订单状态 - 已支付
         */
        public static final String STATUS_PAID = "PAID";
        
        /**
         * 订单状态 - 已取消
         */
        public static final String STATUS_CANCELLED = "CANCELLED";
        
        /**
         * 订单状态 - 已退款
         */
        public static final String STATUS_REFUNDED = "REFUNDED";
        
        /**
         * 订单超时时间（30分钟）
         */
        public static final Long ORDER_TIMEOUT = 30 * 60L;
    }

    /**
     * 用户相关常量
     */
    public static final class USER {
        /**
         * 用户状态 - 正常
         */
        public static final String STATUS_NORMAL = "NORMAL";
        
        /**
         * 用户状态 - 禁用
         */
        public static final String STATUS_DISABLED = "DISABLED";
        
        /**
         * 默认头像
         */
        public static final String DEFAULT_AVATAR = "https://example.com/default-avatar.png";
        
        /**
         * 性别 - 男
         */
        public static final String GENDER_MALE = "MALE";
        
        /**
         * 性别 - 女
         */
        public static final String GENDER_FEMALE = "FEMALE";
        
        /**
         * 性别 - 未知
         */
        public static final String GENDER_UNKNOWN = "UNKNOWN";
    }

    /**
     * 课程相关常量
     */
    public static final class COURSE {
        /**
         * 课程状态 - 草稿
         */
        public static final String STATUS_DRAFT = "DRAFT";
        
        /**
         * 课程状态 - 已发布
         */
        public static final String STATUS_PUBLISHED = "PUBLISHED";
        
        /**
         * 课程状态 - 已下线
         */
        public static final String STATUS_OFFLINE = "OFFLINE";
        
        /**
         * 课程类型 - 免费
         */
        public static final String TYPE_FREE = "FREE";
        
        /**
         * 课程类型 - 付费
         */
        public static final String TYPE_PAID = "PAID";
        
        /**
         * 课程难度 - 初级
         */
        public static final String LEVEL_BEGINNER = "BEGINNER";
        
        /**
         * 课程难度 - 中级
         */
        public static final String LEVEL_INTERMEDIATE = "INTERMEDIATE";
        
        /**
         * 课程难度 - 高级
         */
        public static final String LEVEL_ADVANCED = "ADVANCED";
    }

    /**
     * 文件上传相关常量
     */
    public static final class FILE {
        /**
         * 最大上传文件大小（10MB）
         */
        public static final Long MAX_FILE_SIZE = 10 * 1024 * 1024L;
        
        /**
         * 允许的图片格式
         */
        public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "bmp"};
        
        /**
         * 允许的视频格式
         */
        public static final String[] ALLOWED_VIDEO_TYPES = {"mp4", "avi", "mov", "wmv", "flv"};
        
        /**
         * 文件上传路径前缀
         */
        public static final String UPLOAD_PATH_PREFIX = "/upload/";
    }

    /**
     * 邮件相关常量
     */
    public static final class EMAIL {
        /**
         * 邮件验证码主题
         */
        public static final String CAPTCHA_SUBJECT = "学途教育平台验证码";
        
        /**
         * 重置密码邮件主题
         */
        public static final String RESET_PASSWORD_SUBJECT = "学途教育平台密码重置";
    }

    /**
     * 默认值
     */
    public static final class DEFAULT {
        /**
         * 默认密码
         */
        public static final String PASSWORD = "123456";
        
        /**
         * 系统管理员角色编码
         */
        public static final String ADMIN_ROLE_CODE = "ADMIN";
        
        /**
         * 普通用户角色编码
         */
        public static final String USER_ROLE_CODE = "USER";
        
        /**
         * 教师角色编码
         */
        public static final String TEACHER_ROLE_CODE = "TEACHER";
    }
}