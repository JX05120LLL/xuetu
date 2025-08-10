package com.star.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 * @author star
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回结果
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功返回结果
     * @param data 返回数据
     */
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    /**
     * 成功返回结果
     * @param data 返回数据
     * @param message 返回消息
     */
    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    /**
     * 失败返回结果
     * @param resultCode 结果状态码
     */
    public static <T> R<T> error(ResultCode resultCode) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMessage(resultCode.getMessage());
        return r;
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 返回消息
     */
    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 失败返回结果
     * @param message 返回消息
     */
    public static <T> R<T> error(String message) {
        return error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    /**
     * 是否成功
     */
    public Boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}