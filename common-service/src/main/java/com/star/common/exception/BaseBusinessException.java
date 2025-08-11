package com.star.common.exception;

import com.star.common.result.ResultCode;
import lombok.Getter;

/**
 * 基础业务异常类 - 所有业务异常的父类
 * 
 * 设计原则:
 * 1. 提供统一的异常基础结构
 * 2. 各微服务继承此类创建特定异常
 * 3. 不包含具体业务逻辑
 * 
 * @author star
 */
@Getter
public abstract class BaseBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer code;

    /**
     * 错误信息
     */
    protected String message;

    protected BaseBusinessException() {
        super();
    }

    protected BaseBusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }

    protected BaseBusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    protected BaseBusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    protected BaseBusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    protected BaseBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }

    protected BaseBusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}