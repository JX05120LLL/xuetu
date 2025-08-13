package com.star.course.exception;

import com.star.common.exception.BaseExceptionHandler;
import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 课程服务异常处理器
 * 继承公共异常处理器，专门处理课程业务异常
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class CourseExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理课程服务异常
     */
    @ExceptionHandler(CourseServiceException.class)
    public R<Void> handleCourseServiceException(CourseServiceException e) {
        log.error("课程服务异常: {}", e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理课程未找到异常
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public R<Void> handleCourseNotFoundException(CourseNotFoundException e) {
        log.warn("课程未找到: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理分类异常
     */
    @ExceptionHandler(CategoryException.class)
    public R<Void> handleCategoryException(CategoryException e) {
        log.warn("分类异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理章节异常
     */
    @ExceptionHandler(ChapterException.class)
    public R<Void> handleChapterException(ChapterException e) {
        log.warn("章节异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理课时异常
     */
    @ExceptionHandler(LessonException.class)
    public R<Void> handleLessonException(LessonException e) {
        log.warn("课时异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理评论异常
     */
    @ExceptionHandler(CommentException.class)
    public R<Void> handleCommentException(CommentException e) {
        log.warn("评论异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理回复异常
     */
    @ExceptionHandler(ReplyException.class)
    public R<Void> handleReplyException(ReplyException e) {
        log.warn("回复异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }
}