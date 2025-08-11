package com.star.user.exception;

import com.star.common.result.ResultCode;

/**
 * 用户注册异常类
 * 
 * 专门用于处理用户注册相关的异常，如：
 * - 用户名已存在
 * - 邮箱已存在
 * - 手机号已存在
 * - 注册参数验证失败
 * 
 * @author star
 */
public class UserRegistrationException extends UserServiceException {

    /**
     * 构造函数 - 使用PARAM_ERROR错误码
     * 
     * @param message 异常消息
     */
    public UserRegistrationException(String message) {
        super(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 构造函数 - 包装其他异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public UserRegistrationException(String message, Throwable cause) {
        super(ResultCode.PARAM_ERROR.getCode(), message, cause);
    }

    // 常用的静态工厂方法
    
    /**
     * 用户名已存在异常
     */
    public static UserRegistrationException usernameExists(String username) {
        return new UserRegistrationException("用户名 '" + username + "' 已存在");
    }

    /**
     * 邮箱已存在异常
     */
    public static UserRegistrationException emailExists(String email) {
        return new UserRegistrationException("邮箱 '" + email + "' 已被注册");
    }

    /**
     * 手机号已存在异常
     */
    public static UserRegistrationException phoneExists(String phone) {
        return new UserRegistrationException("手机号 '" + phone + "' 已被注册");
    }

    /**
     * 密码确认不匹配异常
     */
    public static UserRegistrationException passwordMismatch() {
        return new UserRegistrationException("两次输入的密码不一致");
    }

    /**
     * 参数验证失败异常
     */
    public static UserRegistrationException validationFailed(String field, String message) {
        return new UserRegistrationException(field + "验证失败: " + message);
    }
}