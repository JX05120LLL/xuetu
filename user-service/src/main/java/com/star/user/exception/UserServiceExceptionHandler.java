package com.star.user.exception;

import com.star.common.exception.BaseExceptionHandler;
import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 用户服务全局异常处理器
 * 
 * 继承公共模块的BaseExceptionHandler，获得通用异常处理能力
 * 同时添加用户服务特有的异常处理逻辑
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class UserServiceExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理用户服务业务异常
     * 
     * @param e 用户服务异常
     * @return 统一响应结果
     */
    @ExceptionHandler(UserServiceException.class)
    public R<Void> handleUserServiceException(UserServiceException e) {
        log.warn("用户服务业务异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理用户认证异常
     * 
     * @param e 用户认证异常
     * @return 统一响应结果
     */
    @ExceptionHandler(UserAuthException.class)
    public R<Void> handleUserAuthException(UserAuthException e) {
        log.warn("用户认证异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理用户注册异常
     * 
     * @param e 用户注册异常
     * @return 统一响应结果
     */
    @ExceptionHandler(UserRegistrationException.class)
    public R<Void> handleUserRegistrationException(UserRegistrationException e) {
        log.warn("用户注册异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    // 移除了对RuntimeException的处理，避免与BaseExceptionHandler冲突
    // JWT相关的异常应该在Service层转换为具体的业务异常（如UserAuthException）
}