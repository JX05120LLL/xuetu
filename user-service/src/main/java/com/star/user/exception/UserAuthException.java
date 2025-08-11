package com.star.user.exception;

import com.star.common.result.ResultCode;

/**
 * 用户认证异常类
 * 
 * 专门用于处理用户认证相关的异常，如：
 * - 用户名或密码错误
 * - 账号被禁用
 * - Token无效或过期
 * - 权限不足
 * 
 * @author star
 */
public class UserAuthException extends UserServiceException {

    /**
     * 构造函数 - 使用UNAUTHORIZED错误码
     * 
     * @param message 异常消息
     */
    public UserAuthException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 构造函数 - 包装其他异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public UserAuthException(String message, Throwable cause) {
        super(ResultCode.UNAUTHORIZED.getCode(), message, cause);
    }

    // 常用的静态工厂方法
    
    /**
     * 用户不存在异常
     */
    public static UserAuthException userNotFound() {
        return new UserAuthException("用户不存在");
    }

    /**
     * 密码错误异常
     */
    public static UserAuthException wrongPassword() {
        return new UserAuthException("密码错误");
    }

    /**
     * 账号被禁用异常
     */
    public static UserAuthException accountDisabled() {
        return new UserAuthException("账号已被禁用");
    }

    /**
     * Token无效异常
     */
    public static UserAuthException invalidToken() {
        return new UserAuthException("Token无效或已过期");
    }

    /**
     * 权限不足异常
     */
    public static UserAuthException insufficientPermissions() {
        return new UserAuthException("权限不足");
    }
}