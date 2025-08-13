package com.star.course.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 回复异常
 * 
 * @author star
 */
public class ReplyException extends BaseBusinessException {

    public ReplyException(String message) {
        super(message);
    }

    public ReplyException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 回复不存在
     */
    public static ReplyException notFound(Long replyId) {
        return new ReplyException(40408, "回复不存在，ID: " + replyId);
    }

    /**
     * 回复不存在（自定义消息）
     */
    public static ReplyException notFound(String message) {
        return new ReplyException(40408, message);
    }

    /**
     * 回复权限不足
     */
    public static ReplyException permissionDenied(String action) {
        return new ReplyException(40302, "回复权限不足: " + action);
    }

    /**
     * 回复内容不合规
     */
    public static ReplyException contentInvalid(String reason) {
        return new ReplyException(40014, "回复内容不合规: " + reason);
    }

    /**
     * 回复已被删除
     */
    public static ReplyException alreadyDeleted(Long replyId) {
        return new ReplyException(40015, "回复已被删除，ID: " + replyId);
    }

    /**
     * 不能回复自己的评论
     */
    public static ReplyException cannotReplyToSelf() {
        return new ReplyException(40016, "不能回复自己的评论");
    }

    /**
     * 回复目标用户不存在
     */
    public static ReplyException targetUserNotFound(Long userId) {
        return new ReplyException(40017, "回复目标用户不存在，ID: " + userId);
    }

    /**
     * 回复层级过深
     */
    public static ReplyException levelTooDeep() {
        return new ReplyException(40018, "回复层级过深，不允许更多层级的回复");
    }

    /**
     * 回复频率过快
     */
    public static ReplyException tooFrequent() {
        return new ReplyException(42902, "回复过于频繁，请稍后再试");
    }
}