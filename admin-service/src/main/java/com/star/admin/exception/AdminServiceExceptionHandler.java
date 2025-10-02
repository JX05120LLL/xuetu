package com.star.admin.exception;

import com.star.common.exception.BusinessException;
import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 后台管理服务统一异常处理
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class AdminServiceExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        return R.error(e.getMessage());
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.error("系统错误：" + e.getMessage());
    }
}
