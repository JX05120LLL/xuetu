package com.star.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果状态码枚举
 * @author star
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(400, "操作失败"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证"),

    /**
     * 未授权
     */
    FORBIDDEN(403, "未授权"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(401, "用户名或密码错误"),

    /**
     * Token无效
     */
    TOKEN_INVALID(401, "Token无效"),

    /**
     * Token过期
     */
    TOKEN_EXPIRED(401, "Token过期"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(404, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_EXIST(400, "用户已存在"),

    /**
     * 课程不存在
     */
    COURSE_NOT_EXIST(404, "课程不存在"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(404, "订单不存在"),

    /**
     * 库存不足
     */
    STOCK_NOT_ENOUGH(400, "库存不足");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}