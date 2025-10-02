package com.star.ai.exception;

/**
 * AI服务异常
 * 
 * @author star
 */
public class AIServiceException extends RuntimeException {

    public AIServiceException(String message) {
        super(message);
    }

    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AIServiceException apiError(String message) {
        return new AIServiceException("AI API调用失败: " + message);
    }

    public static AIServiceException timeout() {
        return new AIServiceException("AI服务响应超时，请稍后重试");
    }

    public static AIServiceException invalidResponse() {
        return new AIServiceException("AI返回数据格式错误");
    }
}
