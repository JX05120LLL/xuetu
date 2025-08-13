package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 章节异常
 * 
 * @author star
 */
public class ChapterException extends BaseBusinessException {

    public ChapterException(String message) {
        super(message);
    }

    public ChapterException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 章节不存在
     */
    public static ChapterException notFound(Long chapterId) {
        return new ChapterException(40409, "章节不存在，ID: " + chapterId);
    }

    /**
     * 章节不存在（自定义消息）
     */
    public static ChapterException notFound(String message) {
        return new ChapterException(40409, message);
    }

    /**
     * 章节下有课时，无法删除
     */
    public static ChapterException hasLessonsCannotDelete(Long chapterId) {
        return new ChapterException(40019, "章节下存在课时，无法删除，章节ID: " + chapterId);
    }

    /**
     * 章节标题已存在
     */
    public static ChapterException titleExists(String title, Long courseId) {
        return new ChapterException(40020, "章节标题在课程中已存在: " + title + "，课程ID: " + courseId);
    }

    /**
     * 章节数量超限
     */
    public static ChapterException tooManyChapters(Long courseId) {
        return new ChapterException(40021, "课程章节数量已达上限，课程ID: " + courseId);
    }

    /**
     * 章节排序错误
     */
    public static ChapterException sortError(String message) {
        return new ChapterException(40022, "章节排序错误: " + message);
    }
}