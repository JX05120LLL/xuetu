package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 课时异常
 * 
 * @author star
 */
public class LessonException extends BaseBusinessException {

    public LessonException(String message) {
        super(message);
    }

    public LessonException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 课时不存在
     */
    public static LessonException notFound(Long lessonId) {
        return new LessonException(40406, "课时不存在，ID: " + lessonId);
    }

    /**
     * 课时不存在（自定义消息）
     */
    public static LessonException notFound(String message) {
        return new LessonException(40406, message);
    }

    /**
     * 章节下课时数量超限
     */
    public static LessonException tooManyLessons(Long chapterId) {
        return new LessonException(40005, "章节下课时数量已达上限，章节ID: " + chapterId);
    }

    /**
     * 课时排序错误
     */
    public static LessonException sortError(String message) {
        return new LessonException(40006, "课时排序错误: " + message);
    }

    /**
     * 课时视频格式不支持
     */
    public static LessonException unsupportedVideoFormat(String format) {
        return new LessonException(40007, "不支持的视频格式: " + format);
    }

    /**
     * 课时时长无效
     */
    public static LessonException invalidDuration(Integer duration) {
        return new LessonException(40008, "课时时长无效: " + duration + "分钟");
    }
}