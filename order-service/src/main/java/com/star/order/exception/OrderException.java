package com.star.order.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 订单异常
 * 
 * @author star
 */
public class OrderException extends BaseBusinessException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 订单不存在
     */
    public static OrderException notFound(Long orderId) {
        return new OrderException(50001, "订单不存在，ID: " + orderId);
    }

    /**
     * 订单号不存在
     */
    public static OrderException notFoundByOrderNo(String orderNo) {
        return new OrderException(50002, "订单不存在，订单号: " + orderNo);
    }

    /**
     * 订单状态不允许操作
     */
    public static OrderException invalidStatus(String operation, String currentStatus) {
        return new OrderException(50003, String.format("订单状态为[%s]，不允许[%s]操作", currentStatus, operation));
    }

    /**
     * 课程不存在或已下架
     */
    public static OrderException courseNotAvailable(Long courseId) {
        return new OrderException(50004, "课程不存在或已下架，课程ID: " + courseId);
    }

    /**
     * 用户已购买该课程
     */
    public static OrderException courseAlreadyPurchased(Long courseId) {
        return new OrderException(50005, "用户已购买该课程，课程ID: " + courseId);
    }

    /**
     * 订单金额计算错误
     */
    public static OrderException amountCalculationError() {
        return new OrderException(50006, "订单金额计算错误，请重新下单");
    }

    /**
     * 订单创建失败
     */
    public static OrderException createFailed(String reason) {
        return new OrderException(50007, "订单创建失败: " + reason);
    }

    /**
     * 订单支付超时
     */
    public static OrderException paymentTimeout() {
        return new OrderException(50008, "订单支付超时，订单已自动取消");
    }

    /**
     * 库存不足
     */
    public static OrderException insufficientStock(Long courseId) {
        return new OrderException(50009, "课程库存不足，课程ID: " + courseId);
    }

    /**
     * 订单不属于当前用户
     */
    public static OrderException notBelongToUser(Long orderId, Long userId) {
        return new OrderException(50010, String.format("订单[%d]不属于用户[%d]", orderId, userId));
    }
}