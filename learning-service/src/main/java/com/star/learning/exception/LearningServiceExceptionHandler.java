package com.star.learning.exception;

import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 学习服务异常处理器
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class LearningServiceExceptionHandler {

    /**
     * 处理学习服务异常
     */
    @ExceptionHandler(LearningServiceException.class)
    public R<Object> handleLearningServiceException(LearningServiceException e) {
        log.error("学习服务异常: {}", e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return R.error("系统异常，请稍后重试");
    }
}