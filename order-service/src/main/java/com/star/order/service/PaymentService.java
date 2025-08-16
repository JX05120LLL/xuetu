package com.star.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.order.dto.PaymentRecordDTO;
import com.star.order.dto.PaymentRequest;
import com.star.order.entity.PaymentRecord;

import java.util.List;

/**
 * 支付服务接口
 * 
 * @author star
 */
public interface PaymentService extends IService<PaymentRecord> {

    /**
     * 创建支付记录
     * 
     * @param userId 用户ID
     * @param request 支付请求
     * @return 支付记录详情
     */
    PaymentRecordDTO createPayment(Long userId, PaymentRequest request);

    /**
     * 模拟支付（直接支付成功）
     * 
     * @param paymentNo 支付单号
     * @return 支付结果
     */
    PaymentRecordDTO simulatePayment(String paymentNo);

    /**
     * 根据支付单号查询支付记录
     * 
     * @param paymentNo 支付单号
     * @return 支付记录详情
     */
    PaymentRecordDTO getPaymentByPaymentNo(String paymentNo);

    /**
     * 查询订单的支付记录列表
     * 
     * @param orderId 订单ID
     * @return 支付记录列表
     */
    List<PaymentRecordDTO> getPaymentsByOrderId(Long orderId);

    /**
     * 处理支付回调
     * 
     * @param paymentNo 支付单号
     * @param callbackData 回调数据
     * @return 是否成功
     */
    Boolean handlePaymentCallback(String paymentNo, String callbackData);

    /**
     * 申请退款
     * 
     * @param paymentNo 支付单号
     * @param reason 退款原因
     * @return 是否成功
     */
    Boolean requestRefund(String paymentNo, String reason);

    /**
     * 检查支付状态
     * 
     * @param paymentNo 支付单号
     * @return 支付状态
     */
    Integer checkPaymentStatus(String paymentNo);
}