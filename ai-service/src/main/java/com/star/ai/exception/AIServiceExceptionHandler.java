package com.star.ai.exception;

import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

/**
 * AI服务全局异常处理器
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class AIServiceExceptionHandler {

    /**
     * AI服务异常
     */
    @ExceptionHandler(AIServiceException.class)
    public R<Void> handleAIServiceException(AIServiceException e) {
        log.error("AI服务异常", e);
        return R.error(500, e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error("参数校验失败", e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.error(400, "参数错误: " + message);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("参数校验失败", e);
        return R.error(400, "参数错误: " + e.getMessage());
    }

    /**
     * 静态资源不存在（如 favicon.ico），不记录为系统异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public R<Void> handleNoResourceFound(NoResourceFoundException e) {
        if (e.getResourcePath() != null && e.getResourcePath().equals("favicon.ico")) {
            return R.error(404, null);
        }
        log.warn("资源未找到: {}", e.getResourcePath());
        return R.error(404, "资源未找到");
    }

    /**
     * 通用异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.error(500, "系统异常: " + e.getMessage());
    }
}
