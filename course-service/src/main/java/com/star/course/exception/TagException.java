package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 课程标签异常
 * 
 * @author star
 */
public class TagException extends BaseBusinessException {

    public TagException(String message) {
        super(message);
    }

    public TagException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 标签不存在
     */
    public static TagException notFound(Long tagId) {
        return new TagException(40406, "标签不存在，ID: " + tagId);
    }

    /**
     * 标签名称已存在
     */
    public static TagException nameExists(String name) {
        return new TagException(40006, "标签名称已存在: " + name);
    }

    /**
     * 标签被课程引用，无法删除
     */
    public static TagException usedByCoursesCannotDelete(Long tagId) {
        return new TagException(40007, "标签被课程引用，无法删除，标签ID: " + tagId);
    }

    /**
     * 标签名称不能为空
     */
    public static TagException nameCannotBeEmpty() {
        return new TagException(40008, "标签名称不能为空");
    }

    /**
     * 标签名称长度超出限制
     */
    public static TagException nameTooLong() {
        return new TagException(40009, "标签名称长度不能超过20个字符");
    }

    /**
     * 颜色格式错误
     */
    public static TagException invalidColorFormat() {
        return new TagException(40010, "颜色格式错误，应为十六进制颜色值，如：#FF5722");
    }

    /**
     * 课程标签数量超出限制
     */
    public static TagException tooManyTagsForCourse() {
        return new TagException(40011, "每个课程最多只能设置10个标签");
    }
}