package com.star.common.exception;

import com.star.common.result.R;
import com.star.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 基础异常处理器 - 各微服务可继承此类
 * 
 * 职责:
 * 1. 处理通用异常类型
 * 2. 提供异常处理模板
 * 3. 不直接使用@RestControllerAdvice注解
 * 4. 各微服务继承后添加@RestControllerAdvice注解
 * 
 * @author star
 */
@Slf4j
public abstract class BaseExceptionHandler {

    /**
     * 处理基础业务异常
     */
    @ExceptionHandler(BaseBusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBaseBusinessException(BaseBusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理通用业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBusinessException(BusinessException e) {
        log.error("通用业务异常: {}", e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常 - @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数验证异常: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }
        
        return R.error(ResultCode.PARAM_ERROR.getCode(), errorMsg.toString());
    }

    /**
     * 处理参数验证异常 - @ModelAttribute
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        log.error("参数绑定异常: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getFieldErrors()) {
            errorMsg.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }
        
        return R.error(ResultCode.PARAM_ERROR.getCode(), errorMsg.toString());
    }

    /**
     * 处理参数验证异常 - @RequestParam
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束验证异常: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        }
        
        return R.error(ResultCode.PARAM_ERROR.getCode(), errorMsg.toString());
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: {}", e.getMessage(), e);
        return R.error(ResultCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return R.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return R.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统异常，请联系管理员");
    }
}