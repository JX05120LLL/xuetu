package com.star.common.exception;

import com.star.common.result.ResultCode;

/**
 * 系统异常 - 系统级错误
 * @author star
 */
public class SystemException extends BaseBusinessException {

    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public SystemException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, cause);
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode);
    }

    public SystemException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }
}
