package com.star.learning.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 学习服务异常
 * 
 * @author star
 */
public class LearningServiceException extends BaseBusinessException {

    public LearningServiceException(String message) {
        super(message);
    }

    public LearningServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LearningServiceException(Integer code, String message) {
        super(code, message);
    }

    // 常用异常工厂方法
    public static LearningServiceException notFound(String resource) {
        return new LearningServiceException(404, resource + "不存在");
    }

    public static LearningServiceException accessDenied(String message) {
        return new LearningServiceException(403, message);
    }

    public static LearningServiceException createFailed(String message) {
        return new LearningServiceException(500, "创建失败: " + message);
    }

    public static LearningServiceException updateFailed(String message) {
        return new LearningServiceException(500, "更新失败: " + message);
    }

    public static LearningServiceException deleteFailed(String message) {
        return new LearningServiceException(500, "删除失败: " + message);
    }

    public static LearningServiceException paramError(String message) {
        return new LearningServiceException(400, "参数错误: " + message);
    }
}