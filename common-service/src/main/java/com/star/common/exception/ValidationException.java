package com.star.common.exception;

import com.star.common.result.ResultCode;

/**
 * 参数验证异常
 * @author star
 */
public class ValidationException extends BaseBusinessException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(ResultCode.PARAM_ERROR.getCode(), message);
    }

    public ValidationException(String field, String message) {
        super(ResultCode.PARAM_ERROR.getCode(), field + ": " + message);
    }

    public ValidationException(ResultCode resultCode) {
        super(resultCode);
    }

    public ValidationException(String message, Throwable cause) {
        super(ResultCode.PARAM_ERROR.getCode(), message, cause);
    }
}
