package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 评论异常
 * 
 * @author star
 */
public class CommentException extends BaseBusinessException {

    public CommentException(String message) {
        super(message);
    }

    public CommentException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 评论不存在
     */
    public static CommentException notFound(Long commentId) {
        return new CommentException(40407, "评论不存在，ID: " + commentId);
    }

    /**
     * 评论不存在（自定义消息）
     */
    public static CommentException notFound(String message) {
        return new CommentException(40407, message);
    }

    /**
     * 评论权限不足
     */
    public static CommentException permissionDenied(String action) {
        return new CommentException(40301, "评论权限不足: " + action);
    }

    /**
     * 评论内容不合规
     */
    public static CommentException contentInvalid(String reason) {
        return new CommentException(40009, "评论内容不合规: " + reason);
    }

    /**
     * 评论已被删除
     */
    public static CommentException alreadyDeleted(Long commentId) {
        return new CommentException(40010, "评论已被删除，ID: " + commentId);
    }

    /**
     * 评论正在审核中
     */
    public static CommentException underReview(Long commentId) {
        return new CommentException(40011, "评论正在审核中，ID: " + commentId);
    }

    /**
     * 重复点赞
     */
    public static CommentException duplicateLike() {
        return new CommentException(40012, "您已经点过赞了");
    }

    /**
     * 评分超出范围
     */
    public static CommentException invalidRating(Integer rating) {
        return new CommentException(40013, "评分超出范围(1-5): " + rating);
    }

    /**
     * 评论频率过快
     */
    public static CommentException tooFrequent() {
        return new CommentException(42901, "评论过于频繁，请稍后再试");
    }
}