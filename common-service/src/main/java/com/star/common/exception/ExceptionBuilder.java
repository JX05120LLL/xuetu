package com.star.common.exception;

import com.star.common.result.ResultCode;

/**
 * 异常构建工具类
 * @author star
 */
public class ExceptionBuilder {

    /**
     * 构建业务异常
     */
    public static BusinessException buildBusinessException(String message) {
        return new BusinessException(message);
    }

    /**
     * 构建业务异常
     */
    public static BusinessException buildBusinessException(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    /**
     * 构建系统异常
     */
    public static SystemException buildSystemException(String message) {
        return new SystemException(message);
    }

    /**
     * 构建系统异常
     */
    public static SystemException buildSystemException(String message, Throwable cause) {
        return new SystemException(message, cause);
    }

    /**
     * 构建验证异常
     */
    public static ValidationException buildValidationException(String message) {
        return new ValidationException(message);
    }

    /**
     * 构建验证异常
     */
    public static ValidationException buildValidationException(String field, String message) {
        return new ValidationException(field, message);
    }
}
