package com.star.common.exception;

import com.star.common.result.ResultCode;

/**
 * 通用业务异常 - 公共模块使用的业务异常
 * 继承自BaseBusinessException，各微服务可以直接使用或继承
 * 
 * @author star
 */
public class BusinessException extends BaseBusinessException {

    private static final long serialVersionUID = 1L;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}