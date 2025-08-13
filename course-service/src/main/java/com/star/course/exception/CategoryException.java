package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 课程分类异常
 * 
 * @author star
 */
public class CategoryException extends BaseBusinessException {

    public CategoryException(String message) {
        super(message);
    }

    public CategoryException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 分类不存在
     */
    public static CategoryException notFound(Long categoryId) {
        return new CategoryException(40405, "分类不存在，ID: " + categoryId);
    }

    /**
     * 分类名称已存在
     */
    public static CategoryException nameExists(String name) {
        return new CategoryException(40001, "分类名称已存在: " + name);
    }

    /**
     * 分类下有课程，无法删除
     */
    public static CategoryException hasCoursesCannotDelete(Long categoryId) {
        return new CategoryException(40002, "分类下存在课程，无法删除，分类ID: " + categoryId);
    }

    /**
     * 分类层级过深
     */
    public static CategoryException levelTooDeep() {
        return new CategoryException(40003, "分类层级不能超过3级");
    }

    /**
     * 不能设置自己为父分类
     */
    public static CategoryException cannotSetSelfAsParent() {
        return new CategoryException(40004, "不能设置自己为父分类");
    }
}