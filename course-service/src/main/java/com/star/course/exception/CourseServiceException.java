package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 课程服务通用业务异常
 * 
 * @author star
 */
public class CourseServiceException extends BaseBusinessException {

    public CourseServiceException(String message) {
        super(message);
    }

    public CourseServiceException(Integer code, String message) {
        super(code, message);
    }

    public CourseServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CourseServiceException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * 课程服务内部错误
     */
    public static CourseServiceException internalError(String message) {
        return new CourseServiceException(50001, "课程服务内部错误: " + message);
    }

    /**
     * 参数验证失败
     */
    public static CourseServiceException paramError(String message) {
        return new CourseServiceException(40001, "参数错误: " + message);
    }

    /**
     * 数据访问错误
     */
    public static CourseServiceException dataAccessError(String message) {
        return new CourseServiceException(50002, "数据访问错误: " + message);
    }
}