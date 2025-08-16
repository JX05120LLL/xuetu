package com.star.order.exception;

import com.star.common.exception.BaseExceptionHandler;
import com.star.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 订单服务异常处理器
 * 继承公共异常处理器，专门处理订单业务异常
 * 
 * @author star
 */
@Slf4j
@RestControllerAdvice
public class OrderServiceExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理订单异常
     */
    @ExceptionHandler(OrderException.class)
    public R<Void> handleOrderException(OrderException e) {
        log.warn("订单异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理支付异常
     */
    @ExceptionHandler(PaymentException.class)
    public R<Void> handlePaymentException(PaymentException e) {
        log.warn("支付异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }
}