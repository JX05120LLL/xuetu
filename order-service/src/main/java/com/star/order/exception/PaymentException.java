package com.star.order.exception;

import com.star.common.exception.BaseBusinessException;

/**
 * 支付异常
 * 
 * @author star
 */
public class PaymentException extends BaseBusinessException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 支付记录不存在
     */
    public static PaymentException notFound(String paymentNo) {
        return new PaymentException(51001, "支付记录不存在，支付单号: " + paymentNo);
    }

    /**
     * 支付金额不匹配
     */
    public static PaymentException amountMismatch(String expected, String actual) {
        return new PaymentException(51002, String.format("支付金额不匹配，期望: %s，实际: %s", expected, actual));
    }

    /**
     * 支付方式不支持
     */
    public static PaymentException unsupportedPaymentType(Integer paymentType) {
        return new PaymentException(51003, "不支持的支付方式: " + paymentType);
    }

    /**
     * 支付失败
     */
    public static PaymentException paymentFailed(String reason) {
        return new PaymentException(51004, "支付失败: " + reason);
    }

    /**
     * 重复支付
     */
    public static PaymentException duplicatePayment(String paymentNo) {
        return new PaymentException(51005, "重复支付，支付单号: " + paymentNo);
    }

    /**
     * 支付超时
     */
    public static PaymentException paymentTimeout() {
        return new PaymentException(51006, "支付超时，请重新支付");
    }

    /**
     * 退款失败
     */
    public static PaymentException refundFailed(String reason) {
        return new PaymentException(51007, "退款失败: " + reason);
    }

    /**
     * 支付回调验证失败
     */
    public static PaymentException callbackVerificationFailed() {
        return new PaymentException(51008, "支付回调验证失败");
    }

    /**
     * 支付状态不允许操作
     */
    public static PaymentException invalidStatus(String operation, String currentStatus) {
        return new PaymentException(51009, String.format("支付状态为[%s]，不允许[%s]操作", currentStatus, operation));
    }
}