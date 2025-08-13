package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 课程未找到异常
 * 
 * @author star
 */
public class CourseNotFoundException extends BaseBusinessException {

    public CourseNotFoundException(Long courseId) {
        super(40404, "课程不存在，ID: " + courseId);
    }

    public CourseNotFoundException(String message) {
        super(40404, message);
    }

    /**
     * 课程不存在
     */
    public static CourseNotFoundException notFound(Long courseId) {
        return new CourseNotFoundException(courseId);
    }

    /**
     * 课程不存在（自定义消息）
     */
    public static CourseNotFoundException notFound(String message) {
        return new CourseNotFoundException(message);
    }
}