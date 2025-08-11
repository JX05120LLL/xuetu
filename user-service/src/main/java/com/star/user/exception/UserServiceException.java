package com.star.user.exception;

import com.star.common.exception.BaseBusinessException;
import com.star.common.result.ResultCode;

/**
 * 用户服务业务异常类
 * 
 * 用于处理用户服务相关的业务异常，如：
 * - 用户注册相关异常
 * - 用户登录相关异常
 * - 用户信息管理相关异常
 * - 权限验证相关异常
 * 
 * @author star
 */
public class UserServiceException extends BaseBusinessException {

    /**
     * 构造函数 - 使用默认错误码
     * 
     * @param message 异常消息
     */
    public UserServiceException(String message) {
        super(ResultCode.FAIL, message);
    }

    /**
     * 构造函数 - 指定错误码
     * 
     * @param code 错误码
     * @param message 异常消息
     */
    public UserServiceException(ResultCode code, String message) {
        super(code, message);
    }

    /**
     * 构造函数 - 包装其他异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数 - 完整参数
     * 
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public UserServiceException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}